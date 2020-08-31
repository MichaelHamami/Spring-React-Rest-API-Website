import React from 'react';
import axios from 'axios';
import {Card, Form, Button, Col} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faPlusSquare,faSign} from '@fortawesome/free-solid-svg-icons';



export default class Register extends React.Component {

    constructor(props)
    {
        super(props);
        this.state = 
        {
            username:'',
            email: "",
            role: "",
            avatar:""
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
        const user = {
           email: this.state.email,
           username: this.state.username,
           role: this.state.role,
           avatar: this.state.avatar


        }
        axios.post("/acs/users",user)
            .then((response) => {
                console.log(response.data);
                if (response.data)
                {
                    window.location = '/'
                }
            }).catch((error) => {
                console.error("Error - "+error);
            });
            event.preventDefault();
    }

    render(){
        console.log("render called");
        
        const {email,username,role,avatar} = this.state;


        return (
            <div>
            <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>
                <FontAwesomeIcon icon={faPlusSquare} /> Register
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
                                placeholder="Enter User Email" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridEmail">
                            <Form.Label>User Name</Form.Label>
                            <Form.Control required
                                type="text" name="username"
                                value={username}
                                onChange={this.handleChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter User name" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridRole">
                            <Form.Label>Role</Form.Label>
                            <select class="browser-default custom-select"
                            onChange={this.handleChange}
                            name="role"
                            value={role}
                            defaultValue={role}
                            required
                            placeholder="Please Choose Role"
                            >
                                
                            <option value='' selected>Choose Role</option>
                            <option value="ADMIN">Admin</option>
                            <option value="MANAGER">Manager</option>
                            <option value="PLAYER">Player</option>
                            </select>
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridAvatar">
                            <Form.Label>Avatar</Form.Label>
                            <Form.Control required
                                type="select" name="avatar"
                                value={avatar}
                                onChange={this.handleChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Avatar " />
                        </Form.Group>
                    </Form.Row>
                </Card.Body>
                <Card.Footer style={{"textAlign":"right"}}>
                    <Button size="sm" variant="success" type="submit">
                        <FontAwesomeIcon icon={faSign} /> Register
                    </Button>{' '}
                </Card.Footer>
            </Form>
        </Card>
        </div>

            );
            // <Button size="sm" variant="outline-danger" onClick={this.loginUser.bind(this)}><FontAwesomeIcon icon={faPlusSquare} /></Button>
    };


}