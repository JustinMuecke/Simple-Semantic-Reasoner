import "./App.css";

import { useState, useRef } from "react";
import {sendRequest} from "client.js"

function App() {

const [message, messageChange] = useState("")


const handleMessageChange = (event) => {
  messageChange(event.target.value)
}

const handle = () => {
  sendRequest(message)
}

  return (
    <>
      <h1> Simple Semantic Web Reasoner - QueryUI</h1>     
      <div id="card">
        <div id="Query" >
          <textarea id="QueryInput" name="Query" value={message} onChange={handleMessageChange} placeholder='Insert Query'/>
        </div>
        <div id="Button">
          <button onClick={handle}>
            Request
          </button>
        </div>
      </div>
    </>
  )

}

export default App