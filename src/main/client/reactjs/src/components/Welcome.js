import React, {Component} from 'react';

export default class Welcome extends Component {

    render(){
        let user = localStorage.getItem('user');
        let username = localStorage.getItem('username');

        return (
            <div>
                {
                    user === null ?
                    <h2 style={{fontSize: 60,color: "grey",textAlign: 'center'}}> Welcome to roundMe</h2>
                    :
                    <h2> Welcome {username}</h2>

                }
                {
                    <img src="roundMe.jpg" alt=""/>

                }
             </div>

        );
    }
}