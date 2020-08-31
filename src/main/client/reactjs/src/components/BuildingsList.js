import React, {Component} from 'react';
import {Card, Table, InputGroup, FormControl,ButtonGroup, Button} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faList, faEdit, faTrash, faTimes} from '@fortawesome/free-solid-svg-icons';
import {faStepBackward, faFastBackward, faStepForward, faSearch} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import MyToast from './MyToast';
import {Link} from 'react-router-dom';

export default class BuildingsList extends Component {
    constructor(props){
        super(props);
        this.state = {
            elements : [],
            search : '',
            currentPage : 0,
            elementsPerPage : 5
        };
    }
    componentDidMount(){
        this.findAllBuildings(this.state.currentPage);
    }

    findAllBuildings(currentPage){
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com/search/byType/Building?page="+currentPage+"&size="+this.state.elementsPerPage)
            .then(response => response.data)
            .then((data) => {
                console.log(data);
                this.setState({
                    elements: data
                });
            });

    };

    changePage = event => {
        let targetPage = parseInt(event.target.value);
        if(this.state.search){
            this.searchData(targetPage)
        }else{
            this.findAllBuildings(targetPage);
        }
        
        // this.setState({
        //     [event.target.name]: targetPage
        // });
    };

    firstPage = () => {
        // this.state.currentPage = 0;
        this.setState({
            currentPage: 0
        });
        this.findAllBuildings(0);

    };

    prevPage = () => {
        if(this.state.currentPage > 0) {
            if(this.state.search){
                this.searchData(this.state.currentPage -1)
            }else{
                this.findAllBuildings(this.state.currentPage -1);
            }
            
            // this.setState({
            //     currentPage: this.state.currentPage -1
            // });
        }
    };

    nextPage = () => {
        if(this.state.search){
            this.searchData(this.state.currentPage + 1)
        }else{
            this.findAllBuildings(this.state.currentPage + 1);
        }
        
    };

    deleteBuilding = (element) => {
        console.log("deletApartment called");
        console.log(element);
        const action = 
        {
            type:"deleteSpecific",
            element: {
                "elementId":{
                    "domain":"2020b.ofir.cohen",
                    "id":element.elementId.id
                    }
            },
            invokedBy: { "userId":{
                "domain": "2020b.ofir.cohen",
                "email": "a@gmail.com"
            }}
        }
        console.log(action);
        axios.post("acs/actions",action)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true});
                    setTimeout(() => this.setState({"show":false}), 3000);
                    this.setState({
                        elements: this.state.elements.filter(elementis => elementis.elementId.id !== element.elementId.id)
                    });
                } else {
                    this.setState({"show":false});
                }
            });
    };

    searchChange = event => {
        this.setState({
            [event.target.name] : event.target.value
        });
    };

    cancelChange = () => {
        this.setState({"search" : ''});
        this.findAllBuildings(this.state.currentPage);
    };

    searchData = () => {
        console.log(this.state.search)
        const action = 
        {
            type:"searchElementsByNameAndType",
    
            invokedBy: { "userId":{
                "domain": "2020b.ofir.cohen",
                "email": "m@gmail.com"
            }},
            actionAttributes:{
                "type" : "Building",
                "name" : this.state.search
                
            }
        }
        axios.post("acs/actions",action)
        .then(response => {
            if(response.data != null) {
                console.log(response.data)
                this.setState({
                    elements: response.data
                    
                });
            } else {
                this.setState({"show":false});
            }
        });
    }

    render(){
        const {elements, currentPage, search} = this.state;
        const pageNumCss = {
            width: "45px",
            border: "1px solid #17A2B8",
            color: "#17A2B8",
            textAlign: "center",
            fontWeight: "bold"
        };

        return (
            <div>
                <div style={{"display":this.state.show ? "block" : "none"}}>
                <MyToast show = {this.state.show} message = {"Building Deleted Successfully."} type = {"danger"}/>
                </div>
                <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header> 
                    <div style={{"float":"left"}}> 
                    <FontAwesomeIcon icon={faList}/> Buildings List
                    </div>
                    <div style={{"float":"right"}}>
                        <InputGroup  size="sm">
                            <FormControl placeholder="search" name="search" value={search} 
                            className={" info-border bg-dark text-white"}  onChange={this.searchChange} />
                           
                            <InputGroup.Append>
                                <Button size="sm" variant="outline-info" type="button" onClick={this.searchData}>
                                <FontAwesomeIcon icon={faSearch}/> 
                                </Button>
                                <Button size="sm" variant="outline-info" type="button" onClick={this.cancelChange}>
                                <FontAwesomeIcon icon={faTimes}/> 
                                </Button>
                            </InputGroup.Append>
                        </InputGroup>

                    </div>
                    
                    </Card.Header>
                <Card.Body>
                    <Table bordered hover striped variant="dark">
                        <thead>
                            <tr>
                            <th>Street Name</th>
                            <th>Building Number</th>
                            <th>Type</th>
                            <th>Number of Floors</th>
                            <th>Parking Lot</th>
                            <th>Last Tama Date</th>
                            <th>Intended For Tama Or PinoiBinoi</th>
                            <th>Edit/Delete</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                elements.length === 0 ?
                                <tr align="center">
                                <td colSpan="10">No Buildings Available.</td>
                                </tr> :
                                    elements.map((element) => (
                                <tr key={element.elementId.id}>
                                    <td>{element.name} </td>
                                    <td>{element.elementAttributes.building_No} </td>
                                    <td>{element.type} </td>
                                    <td>{element.elementAttributes.num_Of_Floors} </td>
                                    <td>{element.elementAttributes.Parking_Lot} </td>
                                    <td>{element.elementAttributes.last_Tama_Date} </td>
                                    <td>{element.elementAttributes.intended_For_Tama_Or_PinoiBinoi} </td>
                                    <td>
                                            <ButtonGroup>
                                                <Link to={"edit/Building/"+element.elementId.domain +"/"+element.elementId.id} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '}
                                                <Button size="sm" variant="outline-danger" onClick={this.deleteBuilding.bind(this,element)}><FontAwesomeIcon icon={faTrash} /></Button>
                                            </ButtonGroup>
                                        </td>
                                </tr>
                                ))
                            }
                        </tbody>
                    </Table>
                </Card.Body>
                <Card.Footer>
                        <div style={{"float":"left"}}>
                            Showing Page {currentPage}
                        </div>
                        <div style={{"float":"right"}}>
                            <InputGroup size="sm">
                                <InputGroup.Prepend>
                                    <Button type="button" variant="outline-info" disabled={currentPage === 0 ? true : false}
                                        onClick={this.firstPage}>
                                        <FontAwesomeIcon icon={faFastBackward} /> First
                                    </Button>
                                    <Button type="button" variant="outline-info" disabled={currentPage === 0 ? true : false}
                                        onClick={this.prevPage}>
                                        <FontAwesomeIcon icon={faStepBackward} /> Prev
                                    </Button>
                                </InputGroup.Prepend>
                                <FormControl style={pageNumCss} className={"bg-dark"} name="currentPage" value={currentPage}
                                    onChange={this.changePage}/>
                                <InputGroup.Append>
                                    <Button type="button" variant="outline-info" disabled={elements.length === 0 ? true : false}
                                        onClick={this.nextPage}>
                                        <FontAwesomeIcon icon={faStepForward} /> Next
                                    </Button>
                                </InputGroup.Append>
                            </InputGroup>
                        </div>
                    </Card.Footer>
            </Card>
        </div>
        );
    }
}