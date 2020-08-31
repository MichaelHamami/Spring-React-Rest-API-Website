import React, {Component} from 'react';
import {Card, Form, Button, Col} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faSave, faPlusSquare, faUndo, faEdit} from '@fortawesome/free-solid-svg-icons';
import MyToast from './MyToast';
import axios from 'axios';
import '../App.css';

export default class Building extends Component {

    constructor(props) {
        super(props);
        this.state = this.initialState;
        this.state.show = false;
        this.elementChange = this.elementChange.bind(this);
        this.submitBuilding = this.submitBuilding.bind(this);
    }

    initialState = {
        elementId:'', building_name:'',building_No:'', num_Of_Floors:'',Parking_Lot:'',last_Tama_Date:'', intended_For_Tama_Or_PinoiBinoi :''
    };

    componentDidMount() {
        const elementIDomain = this.props.match.params.elementIdDomain;
        const elementID = this.props.match.params.elementIdId;
        if(elementID) {
            alert("componentDidMount called we get elementID DOMAIN : "+elementIDomain +" id " +elementID);
            this.findBuildingById(elementIDomain,elementID);
        }
        else {
            console.log("no element Id");
        }
    }

    findBuildingById = (elementIDomain,elementID) => {
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+elementIDomain+"/"+elementID)
            .then(response => {
                if(response.data != null) {
                    this.setState({
                        elementId: response.data.elementId,
                        building_name: response.data.name,
                        building_No: response.data.elementAttributes.building_No,
                        num_Of_Floors: response.data.elementAttributes.num_Of_Floors,
                        Parking_Lot: response.data.elementAttributes.Parking_Lot,
                        last_Tama_Date: response.data.elementAttributes.last_Tama_Date,
                        intended_For_Tama_Or_PinoiBinoi: response.data.elementAttributes.intended_For_Tama_Or_PinoiBinoi                      
                    });                    
                }
            }).catch((error) => {
                console.error("Error - "+error);
            });
    };

    updateElement = event => {
        event.preventDefault();
        const Building = {
             elementId:{
                    "domain": "2020b.ofir.cohen",
                    "id": this.state.elementId.id
                },
                type:"Building",
                name: this.state.building_name,
                elementAttributes: {
                    "building_No": this.state.building_No ,
                    "num_Of_Floors": this.state.num_Of_Floors ,
                     "Parking_Lot": this.state.Parking_Lot,
                     "last_Tama_Date": this.state.last_Tama_Date,
                     "intended_For_Tama_Or_PinoiBinoi": this.state.intended_For_Tama_Or_PinoiBinoi
                    }
            };

        axios.put("/acs/elements/2020b.ofir.cohen/m@gmail.com/"+Building.elementId.domain+"/"+Building.elementId.id, Building)
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

    submitBuilding(event) {
        event.preventDefault();
        const Building = {
            type: "Building",
            name: this.state.building_name,
            elementAttributes: {
                "building_No": this.state.building_No ,
                "num_Of_Floors": this.state.num_Of_Floors ,
                 "Parking_Lot": this.state.Parking_Lot,
                 "last_Tama_Date": this.state.last_Tama_Date,
                 "intended_For_Tama_Or_PinoiBinoi": this.state.intended_For_Tama_Or_PinoiBinoi
                }
        };
        console.log(Building);
        axios.post("/acs/elements/2020b.ofir.cohen/m@gmail.com", Building)
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
        const {building_name,num_Of_Floors,intended_For_Tama_Or_PinoiBinoi,Parking_Lot,building_No,last_Tama_Date} = this.state;
        return (
            <div>
            <div style={{"display":this.state.show ? "block" : "none"}}>
            <MyToast show = {this.state.show} message = {this.state.method === "put" ? "Building Updated Successfully." : "Building Saved Successfully."} type = {"success"}/>
            </div>
            <Card className={"border border-dark bg-dark text-white"}>
            <Card.Header>
                <FontAwesomeIcon icon={this.state.elementId ? faEdit : faPlusSquare} /> {this.state.elementId ? "Update Building" : "Add New Building"}
            </Card.Header>
            <Form onReset={this.resetElement} onSubmit={this.state.elementId ? this.updateElement : this.submitBuilding}>
                <Card.Body>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridName">
                            <Form.Label>Street Name</Form.Label>
                            <Form.Control required
                                type="text" name="building_name"
                                value={building_name}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Building Name" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridType">
                            <Form.Label>Building Number</Form.Label>
                            <Form.Control required
                                type="number" name="building_No"
                                pattern="[0-9]*"
                                value={building_No}
                                onInput={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Building Number" />
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridCreatedBy">
                            <Form.Label>Number of Floors</Form.Label>
                            <Form.Control required
                                type="number" name="num_Of_Floors"
                                pattern="[0-9]*"
                                value={num_Of_Floors}
                                onInput={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Number of Floors in the Building" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridActive">
                            <Form.Label>Parking Lot</Form.Label>
                            <select class="select-css"
                                onChange={this.elementChange}
                                name="Parking_Lot"
                                value={Parking_Lot}
                                required
                                placeholder="Enter Yes/no if the Building have Parking Lot"
                                >
                                <option value='' selected>Choose Yes/No</option>
                                <option value="Yes">Yes</option>
                                <option value="No">No</option>
                            </select>
                        </Form.Group>
                    </Form.Row>
                    <Form.Row>
                        <Form.Group as={Col} controlId="formGridLocation">
                            <Form.Label>Last Tama Date</Form.Label>
                            <Form.Control required
                                type="date" name="last_Tama_Date"
                                value={last_Tama_Date}
                                onChange={this.elementChange}
                                className={"bg-dark text-white"}
                                placeholder="Enter Building last Tama Date" />
                        </Form.Group>
                        <Form.Group as={Col} controlId="formGridAttributes">
                            <Form.Label>intended For Tama Or PinoiBinoi</Form.Label>
                            <select className="select-css"
                                onChange={this.elementChange}
                                name="intended_For_Tama_Or_PinoiBinoi"
                                value={intended_For_Tama_Or_PinoiBinoi}
                                required
                                placeholder="Enter yes/no if the building intended For Tama Or PinoiBinoi"
                                >
                                <option value='' selected>Choose Yes/No</option>
                                <option value="Yes">Yes</option>
                                <option value="No">No</option>
                            </select>
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

