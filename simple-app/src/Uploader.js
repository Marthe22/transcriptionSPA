import React, { Component } from 'react';


class Uploader extends Component {
    render() { 
        return(
        <div className="Uploader">
            <label>Upload Your File </label>
            <input type="file" className="form-control" name="file" onChange={this.props.onFileChangeHandler} />
        </div>
        )
    }
}
export default Uploader;