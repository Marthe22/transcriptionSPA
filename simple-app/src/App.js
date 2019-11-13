import React, { Component } from 'react';
import axios from 'axios';
import ODRLpanel from './odrlPanel.js';

class App extends Component {
    state = {
        name: "Marthe",
        filename: "cme-derived-data",
        selectedFile: null,
        SelectedFileText: "not loaded",
        Transcription: 'not transcripted'
    }


    onFileChangeHandler = (e) => {
        e.preventDefault();
        this.setState({
            selectedFile: e.target.files[0]
        });
        console.log(e.target.files[0])
        var myHeaders = new Headers();
        myHeaders.append('Content-Type', 'multipart/form-data; boundary=--------------------------330724286008859985302534');
        var formdata = new FormData();
        formdata.append('file', e.target.files[0], { contentType: 'multipart/form-data' });
        // for (var [key, value] of formdata.entries()) {
        //     console.log(key, value);
        // }


        axios.post('http://localhost:8080/transcript1', formdata, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }).then((response) => {
            this.setState({
                Transcription : response.data.result
            })
                console.log("post response: ", response);
            });
        /////===================================================================
        let reader = new FileReader();
        reader.readAsText(e.target.files[0]);
        reader.onload = () => {
            this.setState(
                { SelectedFileText: reader.result }
            )
            console.log(this.state.SelectedFileText);
        };

    }



    render() {
        return (
            <div className="App">
                <h1>Translate your ODRL license</h1>
                <label>Upload Your File </label>
                <input type="file" className="form-control" name="file" onChange={this.onFileChangeHandler} />

                <ODRLpanel text={this.state.SelectedFileText} />
                <ODRLpanel text={this.state.Transcription} />

            </div>

        );
    }

}

export default App
    ;