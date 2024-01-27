import { SetStateAction, useState } from 'react'
import './App.css'

import { User, JoinResponse } from "./chat_pb";
import { ChatServiceClient } from "./chat_grpc_web_pb";


function App() {
  const [message, setMessage] = useState("")

  const handleMessageChange = (event: { target: { value: SetStateAction<string>; }; }) => {
    setMessage(event.target.value)
  }

  return (
    <>
      <h1> Simple Semantic Web Reasoner - QueryUI</h1>     
      <div id="card">
        <div id="Query" >
          <textarea id="QueryInput" name="Query" value={message} onChange={handleMessageChange} placeholder='Insert Query'/>
        </div>
        <div id="Button">
          <button onClick={export_query}>
            Request
          </button>
        </div>
      </div>
    </>
  )

  function export_query() {
    console.log(message)
  }
}



export default App
