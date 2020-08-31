import React, {Component} from 'react';
import {Card, Form, Button, Col} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faSave, faPlusSquare, faUndo, faEdit} from '@fortawesome/free-solid-svg-icons';
import MyToast from './MyToast';
import axios from 'axios';




export default class Element extends Component {

    constructor(props) {
        super(props);
        this.state = this.initialState;
        this.state.show = false;
        this.elementChange = this.elementChange.bind(this);
        this.submitElement = this.submitElement.bind(this);
    }

    initialState = {
        elementIdDomain:'',elementIdId:'',elementId:'',name:'', type:'', createdBy:'', active:'', location:'', attributes:'',domain:'',email:'',userId:''
    };

    componentDidMount() {
        // const elementID = this.props.match.params.elementId;
        // console.log(elementID);

        const elementIDomain = this.props.match.params.elementIdDomain;
        const elementIDID = this.props.match.params.elementIdId;
        // console.log(elementIDomain);
        // console.log(elementIDID);
        // if(elementID) {
        if(elementIDID) {
            // alert("componentDidMount called we get elementID DOMAIN : "+elementID +" id " +elementID.id);
            alert("componentDidMount called we get elementID DOMAIN : "+elementIDomain +" id " +elementIDID);

            // alert("we call find with : "+elementID);
            // this.findBookById(elementID);
            this.findBookById(elementIDomain,elementIDID);
        }
        else {
            console.log("no element Id");
        }
    }

    // findBookById = (elementID) => {
    findBookById = (elementIDomain,elementIDID) => {
        // axios.get("/acs/elements/userDomain/userEmail/"+elementID.domain+"/"+elementID.id)
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+elementIDomain+"/"+elementIDID)
            .then(response => {
                if(response.data != null) {
                    this.setState({
                        elementId: response.data.elementId,
                        name: response.data.name,
                        type: response.data.type,
                        active: response.data.active,
                        createdBy: response.data.createdBy,
                        location: response.data.location,
                        attributes: response.data.attributes
                        
                    });
                    alert('Name: '+this.state.name+', Type: '+this.state.type+', CreatedBy: '+this.state.createdBy+", Active: "+this.state.active+', Location: '+this.state.location+", Attributes: "+this.state.attributes);
                }
            }).catch((error) => {
                console.error("Error - "+error);
            });
    };
    updateElement = event => {
        event.preventDefault();

        const element = {
            elementId: this.state.elementId,
            domain: "2020B.Ofir.Cohen",
            email: this.state.createdBy,
            userId: {"domain":  "2020B.Ofir.Cohen", "email": this.state.email},
            type: this.state.type,
            name: this.state.name,
            active: this.state.active,
            // createdBy: this.state.userId,
            createdBy: this.userId,
            location: {"lat":this.state.location , "lng":1.1},
            attributes: this.state.attributes
        };

        axios.put("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+element.elementId.domain+"/"+element.elementId.id, element)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true, "method":"put"});
                    setTimeout(() => this.setState({"show":false}), 3000);
                    setTimeout(() => this.bookList(), 3000);
                } else {
                    this.setState({"show":false});
                }
            });

        this.setState(this.initialState);
    };

    resetElement = () => {
        this.setState(() => this.initialState);
    };

    submitElement(event) {
        alert('Name: '+this.state.name+', Type: '+this.state.type+', CreatedBy: '+this.state.createdBy+", Active: "+this.state.active+', Location: '+this.state.location+", Attributes: "+this.state.attributes);
        event.preventDefault();
        const element = {
            domain: "2020B.Ofir.Cohen",
            email: this.state.createdBy,
            userId: {"domain":  "2020B.Ofir.Cohen" , "email": this.state.email},


            type: this.state.type,
            name: this.state.name,
            active: this.state.active,
            createdBy: this.userId,
            location: {"lat":this.state.location , "lng":1.1},
            attributes: this.state.attributes
        };
        console.log(element);
        axios.post("/acs/elements/2020b.ofir.cohen/m@gmail.com", element)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true, "method":"post"});
                    setTimeout(() => this.setState({"show":false}), 3000);
                } else {
                    this.setState({"show":false});
                }
            });

            
    }

    elementChange(event) {
        this.setState({
            [event.target.name]:event.target.value
        });
    }

    render(){
        const {name,type,createdBy,active,location,attributes} = this.state;
        return (
            <div>
            <div style={{"display":this.state.show ? "block" : "none"}}>
            <MyToast show = {this.state.show} message = {this.state.method === "put" ? "Element Updated Successfully." : "Element Saved Successfully."} type = {"success"}/>
            </div>
            <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>
                <FontAwesomeIcon icon={this.state.elementId ? faEdit : faPlusSquare} /> {this.state.elementId ? "Update Element" : "Add New Element"}
            </Card.Header>
            <Form onReset={this.resetElement} onSubmit={this.state.elementId ? this.updateElement : this.submitElement} elementId="elementFormId">
                <Card.Body>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridName">
                            <Form.Label>Name</Form.Label>
                            <Form.Control required
                                type="text" name="name"
                                value={name}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Element Name" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridType">
                            <Form.Label>Type</Form.Label>
                            <Form.Control required
                                type="text" name="type"
                                value={type}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Element Type" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridCreatedBy">
                            <Form.Label>Created By</Form.Label>
                            <Form.Control required
                                type="text" name="createdBy"
                                value={createdBy}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Created By Element " />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridActive">
                            <Form.Label>Active</Form.Label>
                            <Form.Control required
                                type="text" name="active"
                                value={active}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Active Element" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridLocation">
                            <Form.Label>Location</Form.Label>
                            <Form.Control required
                                type="text" name="location"
                                value={location}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Element Location" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridAttributes">
                            <Form.Label>Attributes</Form.Label>
                            <Form.Control
                                type="text" name="attributes"
                                value={attributes}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Element Attributes" />
                        </Form.Group>
                    </Form.Row>
                </Card.Body>
                <Card.Footer style={{"textAlign":"right"}}>
                    <Button size="sm" variant="success" type="submit">
                        <FontAwesomeIcon icon={faSave} /> {this.state.elementId ? "Update" : "Save"}
                    </Button>{' '}
                    <Button size="sm" variant="info" type="reset">
                        <FontAwesomeIcon icon={faUndo} /> Reset
                    </Button>
                </Card.Footer>
            </Form>
        </Card>
        </div>
        );
    }

}

