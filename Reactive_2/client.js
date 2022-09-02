const fs = require('fs');
const net = require('net');

var myID;
var myPort;
var sharedStr;
var network = []
var myTimeStamp = 0
var nodes = []
var nodesID = []
var history = []
var reApply = []
var logger = true
var server
var myOps = []
var realTime = true

function readFromFile(inputPath) {
  try {
    const data = fs.readFileSync(inputPath, 'utf8')

    groups = data.split('\r\n\r\n')

    myself = groups[0].split('\r\n')
    myID = myself[0]
    nodesID.push(myID)
    myPort = myself[1]
    sharedStr = myself[2]

    neighbours = groups[1].split('\r\n')
    neighbours.forEach(neighbour => {
      node = neighbour.split(' ')
      network.push({
        id: node[0],
        host: node[1],
        port: node[2]
      })
    })

    ops = groups[2].split('\r\n')
  }
  catch (e) {
    console.error(e)
  }
}

function sleep(time) {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve('resolve');
    }, time);
  });
}

function applyOP(sender, timestamp, op) {
  // apply operation on local string
  if (logger)
    console.log(`Client ${myID} started merging, from ${timestamp} time stamp, on ${sharedStr}`)

  oldString = sharedStr

  var operation = op.split(' ')

  switch (operation[0]) {
    case "insert":
      letter = operation[1]
      if (operation.length == 3) {
        index = operation[2]
        if (sharedStr.length > index) {
          if (index == 0) {
            sharedStr = letter + sharedStr
          } else {
            sharedStr = sharedStr.slice(0, index) + letter + sharedStr.slice(index)
          }
        } 
      } else {
        sharedStr = sharedStr + letter
      }
      break;

    case "delete":
      index = operation[1]
      if (index < sharedStr.length) {
        if (index == 0) {
          sharedStr = sharedStr.slice(1)
        } else {
          indexP = parseInt(index) + 1
          sharedStr = sharedStr.slice(0, index) + sharedStr.slice(indexP)
        }
      }

      break;

    default:
      console.log('Error: illegal operation : ' + operation)
  }
  history.push({ "sender": sender, "timestamp": timestamp, "operation": op, "oldString": oldString })
}

function recMessage(data) {
  if (data == '') {
    //fix issues with end of messages (empty data)
    return
  }


  // id:timestamp:operation 
  message = data.split(':')

  if (logger)
    console.log(`Client ${myID} received an update operation "${message[2]}";${message[1]} from client ${message[0]}`);

  if (nodesID.indexOf(message[0]) === -1) {
    nodesID.push(message[0])
  }
  if (message[2] === 'goodbye') {
    nodesID = nodesID.filter((o) => (o != message[0]))

    if (nodesID.length == 0) {
      console.log(`client${myID} final string: ${sharedStr}`)
      server.close()
      nodes.map((s) => s.destroy())
    }
  }

  else {
    myTimeStamp = Math.max(myTimeStamp, parseInt(message[1]))
    myTimeStamp++;
    update(message[0], parseInt(message[1]), message[2])
  }
}


function update(sender, timestamp, op) {
  // update string with correct operations time stamps
  if (history.length == 0) {
    applyOP(sender, timestamp, op)
  }
  else {
    while (history.length != 0 && (((history[history.length - 1])["timestamp"] > timestamp) ||
      ((history[history.length - 1])["timestamp"] == timestamp) && (myID > sender))) {
      reApply.push(history.pop())
    }
    if (reApply.length != 0) {
      sharedStr = (reApply[reApply.length - 1])["oldString"]
      if (logger)
        console.log(`Client ${myID} removed operation ${op},${timestamp}  from storage`)
    }
    applyOP(sender, timestamp, op)
    while (reApply.length != 0) {
      curr_op = reApply.pop()
      applyOP(curr_op["sender"], curr_op["timestamp"], curr_op["operation"])
    }
  }
}

function startServer(){
  server = net.createServer((c) => {
    nodes.push(c)
    c.on('data', (data) => {
      messages = data.toString().split("%%")
      for (msg of messages)
        recMessage(msg)
    });
    c.on('end', () => {
    });
  });
  server.on('error', (err) => { throw err; });

  server.listen(myPort, () => {
  });
}

function connectToClients() {
  for (node of network) {
    if (node['id'] > myID) {
      var client = net.createConnection({ port: node['port'] }, () => {
      });
      nodes.push(client)
      client.on('error', (err) => {
        throw err;
      });
      client.on('data', (data) => {
        messages = data.toString().split("%%")
        for (msg of messages)
          recMessage(msg)
      })
    }
  }
}

function sendMessages(receivers, op){
      msg = `${myID}:${myTimeStamp}:${op}%%`
      if (!realTime){
        myOps.push(msg)
        if (myOps.length >= 10 || op == "goodbye" ){
          caten_msg = ""
          for (myOp of myOps){
            caten_msg += myOp
          }
          myOps = []
          for (node of receivers) {
            node.write(caten_msg)
          }
        }
      }
      else {
        // send operation to the network
        for (node of receivers) {
          node.write(msg)
        }
      }
    }

async function main() {
  readFromFile(process.argv.slice(2)[0])

  startServer()

  // busy wait, to run all clients servers 
  await sleep(4000)

  connectToClients()

  await sleep(2000)

  console.log(`network size - ${nodes.length + 1}`)

  //run on local operations, make one , and send it to all other nodes with my time stamp
  for (op of ops) {
    myTimeStamp++;

    // run local operation
    update(myID, myTimeStamp, op)

    sendMessages(nodes, op)

    await sleep(1000)
  }

  if (logger)
    console.log(`Client ${myID} finished his local string modifications`)

  //finished all operations, sending goodbye message
  sendMessages(nodes, "goodbye")
  recMessage(`${myID}:${myTimeStamp}:goodbye`)

  if (logger)
    console.log(`Client ${myID} is exiting`)
}

main()

