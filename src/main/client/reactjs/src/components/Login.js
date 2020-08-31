import React from 'react';
import axios from 'axios';
// import {Route, Redirect , browserHistory} from 'react-router';
import {Card, Form, Button, Col} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlusSquare,faSign} from '@fortawesome/free-solid-svg-icons';
// import MyToast from './MyToast';
// import Welcome from './Welcome';
// import Profile from './Profile';
// import { PageContext } from "./PageContextProvider";





export default class Login extends React.Component {

    constructor(props)
    {
        super(props);
        this.state = 
        {
            user:'',
            email: "",
            domain: "",
            loginErrors: "",
            redirects: null
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);

    }
    handleChange(event) {
        this.setState({
          [event.target.name]: event.target.value
        });
      }
      
    handleSubmit(event) {
        const { email, domain } = this.state;

        console.log("we in handle submit try to login");
        console.log(email);
        console.log(domain);
        axios.get("/acs/users/login/"+domain +"/"+email)
            .then((response) => {
                let data = response.data
                console.log(data);
                localStorage.setItem('user',response.data.userId.email);
                localStorage.setItem('username',response.data.username);

                // this.setState({
                //     email: data.userId.email,
                //     domain: data.userId.domain
                // });
                console.log(data.userId.email);
                if (data.userId.email)
                {
                    console.log(data.userId);
                    window.location = '/'

                }
            }).catch((error) => {
                console.log("Error - "+error);
            });
            event.preventDefault();
    }

    render(){
        console.log("render called");
        const {email,domain} = this.state;
          
        return (
            <div>
            <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>
                <FontAwesomeIcon icon={faPlusSquare} /> Login
            </Card.Header>
            <Form onSubmit={this.handleSubmit}>
                <Card.Body>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridEmail">
                            <Form.Label>Email</Form.Label>
                            <Form.Control required
                                type="text" name="email"
                                value={email}
                                onChange={this.handleChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter User email" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridDomain">
                            <Form.Label>Domain</Form.Label>
                            <Form.Control required
                                type="text" name="domain"
                                value={domain}
                                onChange={this.handleChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter User Domain " />
                        </Form.Group>
                    </Form.Row>
                </Card.Body>
                <Card.Footer style={{"textAlign":"right"}}>
                    <Button size="sm" variant="success" type="submit">
                        <FontAwesomeIcon icon={faSign} /> Login
                    </Button>{' '}
                </Card.Footer>
            </Form>
        </Card>
        </div>

            );
            // <Button size="sm" variant="outline-danger" onClick={this.loginUser.bind(this)}><FontAwesomeIcon icon={faPlusSquare} /></Button>
    };


}