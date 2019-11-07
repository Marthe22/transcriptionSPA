import React, {Component} from 'react';
import axios from 'axios';

class App extends Component {
  state = {
    name : "Marthe",
    filename : "cme-derived-data"
  }
  
  handleChange = (e) => {
    this.setState({
      name:e.target.value
    })
  }
  handleChange2 = (e) => {
    this.setState({
      filename:e.target.value
    })
  }
  handleSubmit = (e) =>{
    e.preventDefault() 
    console.log("form1 submitted", this.state.name)
    axios.get(`http://localhost:8080/greeting?name=${this.state.name}`).then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  handleSubmit2 = (e) =>{
    e.preventDefault() 
    console.log("form2 submitted", this.state.filename)
    axios.get(`http://localhost:8080/transcript1?filename=${this.state.filename}`).then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  componentDidMount(){
    axios.get("http://localhost:8080/greeting?name=Alex").then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });
  }

  render(){
  return (
    <div className="App">
      <h1>Welcome to simple-app, {this.state.name}</h1>
      <form onSubmit = {this.handleSubmit}> 
        <label>
          Name:
        <input type="text" name="name"  onChange = {this.handleChange}/>
        </label>
        <input type="submit" value="Submit" />
      </form>

      <form onSubmit = {this.handleSubmit2}> 
        <label>
          FileName:
        <input type="text" name="filename" onChange = {this.handleChange2}/>
        </label>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
 } 

}

export default App
;