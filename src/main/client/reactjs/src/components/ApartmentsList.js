import React, {Component} from 'react';
import {Card, Table, InputGroup, FormControl,ButtonGroup, Button} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faList, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {faStepBackward, faFastBackward, faStepForward, faSearch, faTimes} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import MyToast from './MyToast';
import {Link} from 'react-router-dom';

export default class ApartmentsList extends Component {
    constructor(props){
        super(props);
        this.state = {
            elements : [],
            currentPage : 0,
            elementsPerPage : 5
        };
    }
    componentDidMount(){
        this.findAllApartments(this.state.currentPage);
    }

    findAllApartments(currentPage){
        console.log(currentPage);
        console.log(this.state.currentPage);
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com/search/byType/Apartment?page="+currentPage+"&size="+this.state.elementsPerPage)
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
        this.setState({
            currentPage: 0
        });
        this.findAllApartments(0);

    };

    prevPage = () => {
        if(this.state.currentPage > 0) {
            if(this.state.search){
                this.searchData( this.state.currentPage -1)
            }else{
                this.findAllBuildings( this.state.currentPage -1);
            }
            
                
        }
    };

    nextPage = () => {
        if(this.state.search){
            this.searchData(this.state.currentPage + 1)
        }else{
            this.findAllBuildings(this.state.currentPage + 1);
        }
        
    };

    deleteApartment = (element) => {
        // there is no delete in our Rest so i will not check it...
        console.log("deletApartment called");
        console.log(element);
        const action = 
        {
            type:"deleteSpecific",
            // element :{"elementId":element.elementId}
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
        this.findAllApartments(this.state.currentPage);
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
                "type" : "Apartment",
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
                <MyToast show = {this.state.show} message = {"Apartment Deleted Successfully."} type = {"danger"}/>
                </div>
                <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header>
                <div style={{"float":"left"}}> 
                    <FontAwesomeIcon icon={faList}/> Apartments List
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
                            {/* <th>ElementDomain</th> */}
                            <th>Owner</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Apartment No</th>
                            <th>Floor</th>
                            <th>Number of Rooms</th>
                            <th>Last Renovation Date</th>
                            <th>Edit/Delete</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                elements.length === 0 ?
                                <tr align="center">
                                <td colSpan="10">No Apartments Available.</td>
                                </tr> :
                                // this.state.elements.map((element) => (
                                    elements.map((element) => (
                                <tr key={element.elementId.id}>
                                    {/* <td> {element.elementId.domain}</td> */}
                                    <td>{element.elementAttributes.owner} </td>
                                    <td>{element.name} </td>
                                    <td>{element.type} </td>
                                    <td>{element.elementAttributes.apartment_No} </td>
                                    <td>{element.elementAttributes.floor} </td>
                                    <td>{element.elementAttributes.number_Of_Rooms} </td>
                                    <td>{element.elementAttributes.last_Renovation_Date} </td>
                                    <td>
                                            <ButtonGroup>
                                                {/* <Link to={"edit/"+element.elementId} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '} */}
                                                {/* <Link to={"edit/${element.elementId}"} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '} */}
                                                <Link to={"edit/Apartment/"+element.elementId.domain +"/"+element.elementId.id} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '}
                                                <Button size="sm" variant="outline-danger" onClick={this.deleteApartment.bind(this,element)}><FontAwesomeIcon icon={faTrash} /></Button>
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
                                    {/* <Button type="button" variant="outline-info" disabled={currentPage === totalPages ? true : false}
                                        onClick={this.lastPage}>
                                        <FontAwesomeIcon icon={faFastForward} /> Last
                                    </Button> */}
                                </InputGroup.Append>
                            </InputGroup>
                        </div>
                    </Card.Footer>
            </Card>
        </div>
        );
    }
}