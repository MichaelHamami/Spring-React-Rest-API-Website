import React, {Component} from 'react';
import {Card, Table, InputGroup, FormControl,ButtonGroup, Button} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faList, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {faStepBackward, faFastBackward, faStepForward} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import MyToast from './MyToast';
import {Link} from 'react-router-dom';




export default class ElementList extends Component {
    constructor(props){
        super(props);
        this.state = {
            elements : [],
            currentPage : 0,
            elementsPerPage : 5
        };
    }
    componentDidMount(){
        this.findAllElements(this.state.currentPage);
    }

    findAllElements(currentPage){
        console.log(currentPage);
        console.log(this.state.currentPage);
        axios.get("/acs/elements/2020b.ofir.cohen/m@gmail.com?page="+currentPage+"&size="+this.state.elementsPerPage)
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
        this.findAllElements(targetPage);
        // this.state.currentPage = targetPage;
        this.setState({
            [event.target.name]: targetPage
        });
    };

    firstPage = () => {
        // this.state.currentPage = 0;
        this.setState({
            currentPage: 0
        });
        this.findAllElements(0);

    };

    prevPage = () => {
        if(this.state.currentPage > 0) {
            this.findAllElements(this.state.currentPage -1);
            this.setState({
                currentPage: this.state.currentPage -1
            });
        }
    };

    // lastPage = () => {
    //     let condition = Math.ceil(this.state.totalElements / this.state.elementsPerPage);
    //     if(this.state.currentPage < condition) {
    //         this.findAllElements(condition);
    //     }
    // };

    nextPage = () => {
        this.setState({
            currentPage: this.state.currentPage +1
        });
        this.findAllElements(this.state.currentPage + 1);
    };

    deleteAllElement = () => {
        axios.delete("/acs/admin/elements/2020b.ofir.cohen/a@gmail.com")
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true});
                    setTimeout(() => this.setState({"show":false}), 3000);
                    this.setState({
                        elements: ""
                    });
                } else {
                    this.setState({"show":false});
                }
            });
    };

    render(){
        const {elements, currentPage} = this.state;
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
                <MyToast show = {this.state.show} message = {"Element Deleted Successfully."} type = {"danger"}/>
                </div>
                <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header><FontAwesomeIcon icon={faList}/>Element List</Card.Header>
                <Card.Body>
                    <Table bordered hover striped variant="dark">
                        <thead>
                            <tr>
                            {/* <th>ElementDomain</th> */}
                            <th>ElementId</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Active</th>
                            <th>Date Created</th>
                            <th>CreatedBy</th>
                            <th>Location</th>
                            <th>Element Attributes</th>
                            <th>Edit</th>

                            </tr>
                        </thead>
                        <tbody>
                            {
                                elements.length === 0 ?
                                <tr align="center">
                                <td colSpan="10">No Elements Available.</td>
                                </tr> :
                                // this.state.elements.map((element) => (
                                    elements.map((element) => (
                                <tr key={element.elementId.id}>
                                    {/* <td> {element.elementId.domain}</td> */}
                                    <td>{element.elementId.id} </td>
                                    <td>{element.name} </td>
                                    <td>{element.type} </td>
                                    {element.active ?
                                    (<td>True</td>)
                                    :
                                    (<td>False</td>)
                                    }
                                    {/* <td>{element.active}</td> */}
                                    <td>{element.createdTimestamp} </td>
                                    <td>UserDomain: {element.createdBy.userId.domain} Email: {element.createdBy.userId.email} </td>
                                    <td>Lat:{element.location.lat} Lng:  {element.location.lng} </td>
                                    <td>{element.elementAttributes} </td>
                                    <td>
                                            <ButtonGroup>
                                                {/* <Link to={"edit/"+element.elementId} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '} */}
                                                {/* <Link to={"edit/${element.elementId}"} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '} */}
                                                <Link to={"edit/"+element.elementId.domain +"/"+element.elementId.id} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '}
                                                <Button size="sm" variant="outline-danger" onClick={this.deleteAllElement.bind(this)}><FontAwesomeIcon icon={faTrash} /></Button>
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