import React, { Component } from 'react';

class ODRLpanel extends Component{
    state = {
        text : ""
    }
    componentDidUpdate(){
        console.log('received props', this.props.text)
    }

    render(){
        return(
            <div className="odrlfile">
                <h1>ODRL panel</h1>
                <textarea name="description" rows = '20' cols = '75' value={this.props.text}  />
            </div>

        )
    }

}
export default ODRLpanel;