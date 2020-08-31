import React, {Component} from 'react';
import {Card, Form, Button, Col} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faSave, faPlusSquare, faUndo, faEdit} from '@fortawesome/free-solid-svg-icons';
import MyToast from './MyToast';
import axios from 'axios';


export default class Apartment extends Component {

    constructor(props) {
        super(props);
        this.state = this.initialState;
        this.state.show = false;
        this.elementChange = this.elementChange.bind(this);
        this.submitApartment = this.submitApartment.bind(this);
    }

    initialState = {
        elementId:'', apartment_name:'',apartment_No:'', floor:'',number_Of_Rooms:'',last_Renovation_Date:'',owner:'', email:'',type:''
    };

    componentDidMount() {
        const elementIDomain = this.props.match.params.elementIdDomain;
        const elementID = this.props.match.params.elementIdId;
        if(elementID) {
            alert("componentDidMount called we get elementID DOMAIN : "+elementIDomain +" id " +elementID);
            this.findApartmentById(elementIDomain,elementID);
        }
        else {
            console.log("no element Id");
        }
    }

    findApartmentById = (elementIDomain,elementID) => {
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+elementIDomain+"/"+elementID)
            .then(response => {
                if(response.data != null) {
                    this.setState({
                        elementId: response.data.elementId,
                        apartment_name: response.data.name,
                        apartment_No: response.data.elementAttributes.apartment_No,
                        floor: response.data.elementAttributes.floor,
                        number_Of_Rooms: response.data.elementAttributes.number_Of_Rooms,
                        last_Renovation_Date: response.data.elementAttributes.last_Renovation_Date,
                        owner: response.data.elementAttributes.owner                      
                    });                    
                }
            }).catch((error) => {
                console.error("Error - "+error);
            });
    };

    updateElement = event => {
        event.preventDefault();
        const Apartment = {
             elementId:{
                    "domain": "2020b.ofir.cohen",
                    "id": this.state.elementId.id
                },
                type:"Apartment",
                name: this.state.apartment_name,
                elementAttributes: {"apartment_No": this.state.apartment_No ,
                    "floor": this.state.floor , "number_Of_Rooms": this.state.number_Of_Rooms,
                     "last_Renovation_Date": this.state.last_Renovation_Date,
                     "owner": this.state.owner}
            };

        axios.put("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+Apartment.elementId.domain+"/"+Apartment.elementId.id, Apartment)
        // axios.put("/acs/elements/2020b.ofir.cohen/m@gmail.com/2020b.ofir.cohen/"+Apartment.elementId.id, Apartment)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true, "method":"put"});
                    setTimeout(() => this.setState({"show":false}), 3000);
                } else {
                    this.setState({"show":false});
                }
            });
        this.setState(this.initialState);
    };

    resetElement = () => {
        this.setState(() => this.initialState);
    };

    submitApartment(event) {
        alert("Submit Apartment");
        event.preventDefault();
        // const {name,floor,number_Of_Rooms,last_Renovation_Date,apartment_No,owner} = this.state;
        const Apartment = {
            type: "Apartment",
            name: this.state.apartment_name,
            elementAttributes: {"apartment_No": this.state.apartment_No ,
                "floor": this.state.floor , "number_Of_Rooms": this.state.number_Of_Rooms,
                 "last_Renovation_Date": this.state.last_Renovation_Date,
                 "owner": this.state.owner}
        };
        console.log(Apartment);
        axios.post("/acs/elements/2020b.ofir.cohen/m@gmail.com", Apartment)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true, "method":"post"});
                    setTimeout(() => this.setState({"show":false}), 3000);
                } else {
                    this.setState({"show":false});
                }
            });

            this.setState(this.initialState);

    }

    elementChange(event) {
        this.setState({
            [event.target.name]:event.target.value
        });
    }

    render(){
        const {apartment_name,floor,number_Of_Rooms,last_Renovation_Date,apartment_No,owner} = this.state;
        return (
            <div>
            <div style={{"display":this.state.show ? "block" : "none"}}>
            <MyToast show = {this.state.show} message = {this.state.method === "put" ? "Apartment Updated Successfully." : "Apartment Saved Successfully."} type = {"success"}/>
            </div>
            <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>
                <FontAwesomeIcon icon={this.state.elementId ? faEdit : faPlusSquare} /> {this.state.elementId ? "Update Apartment" : "Add New Apartment"}
            </Card.Header>
            <Form onReset={this.resetElement} onSubmit={this.state.elementId ? this.updateElement : this.submitApartment} elementId="elementFormId">
            {/* <Form onReset={this.resetElement} onSubmit={this.state.elementId ? this.updateElement : this.submitApartment}> */}
                <Card.Body>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridName">
                            <Form.Label>Name (*)</Form.Label>
                            <Form.Control required
                                type="text" name="apartment_name"
                                value={apartment_name}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Apartment Name" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridType">
                            <Form.Label>Apartment Number (*)</Form.Label>
                            <Form.Control required
                                type="number" name="apartment_No"
                                pattern="[0-9]*"
                                value={apartment_No}
                                onInput={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Apartment Number" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridCreatedBy">
                            <Form.Label>Floor (*)</Form.Label>
                            <Form.Control required
                                type="number" name="floor"
                                pattern="[0-9]*"
                                value={floor}
                                onInput={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Apartment Floor" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridActive">
                            <Form.Label>Last Renovation Date (*)</Form.Label>
                            <Form.Control required
                                type="date" name="last_Renovation_Date"
                                value={last_Renovation_Date}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Apartment Last Renvotion Date" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridLocation">
                            <Form.Label>Owner (*)</Form.Label>
                            <Form.Control required
                                type="text" name="owner"
                                value={owner}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Owner of the Apartment" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridAttributes">
                            <Form.Label>Number of Rooms</Form.Label>
                            <Form.Control
                                type="number" name="number_Of_Rooms"
                                pattern="[0-9]*"
                                value={number_Of_Rooms}
                                onInput={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter The Number of Rooms in the Apartment" />
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

