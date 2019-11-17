import React, { Component } from 'react';


class Uploader extends Component {
    render() { 
        return(
        <div className="Uploader">
            <p>Upload a file to be transcripted</p>
            {/* <label>Upload Your File</label> */}
            <input type="file" className="form-control" name="file" onChange={this.props.onFileChangeHandler} />
        </div>
        )
    }
}
export default Uploader;