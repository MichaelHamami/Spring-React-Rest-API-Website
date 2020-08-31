import React, {Component} from 'react';
import {Card, Table, InputGroup, FormControl,ButtonGroup, Button} from 'react-bootstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faList, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {faStepBackward, faFastBackward, faStepForward} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import {Link} from 'react-router-dom';





export default class Profile extends Component {

    

    constructor(props) {
        super(props);
        this.state = {
            elements : [],
            currentPage : 0,
            elementsPerPage : 5
        };
    }

    componentDidMount(){
        this.findAllElementsOfUser(this.state.currentPage);
    }

    findAllElementsOfUser(currentPage){
        console.log(currentPage);
        let user = localStorage.getItem('user');
        console.log(user);
        const action = 
        {
            type:"searchElementsOfUser",
            invokedBy: { "userId":{
                "domain": "2020b.ofir.cohen",
                "email": user
            }}
        }

        axios.post("acs/actions",action)
            .then(response => {
                if(response.data != null) {
                    this.setState({"show":true});
                    setTimeout(() => this.setState({"show":false}), 3000);
                    this.setState({
                        elements: response.data
                    });
                } else {
                    this.setState({"show":false});
                }
            });

    };

    changePage = event => {
        let targetPage = parseInt(event.target.value);
        this.findAllElementsOfUser(targetPage);
        this.setState({
            [event.target.name]: targetPage
        });
    };

    firstPage = () => {
        this.setState({
            currentPage: 0
        });
        this.findAllElementsOfUser(0);

    };

    prevPage = () => {
        if(this.state.currentPage > 0) {
            this.findAllElementsOfUser(this.state.currentPage -1);
            this.setState({
                currentPage: this.state.currentPage -1
            });
        }
    };

    nextPage = () => {
        this.setState({
            currentPage: this.state.currentPage +1
        });
        this.findAllElementsOfUser(this.state.currentPage + 1);
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
                <Card className={"border border-dark bg-dark text-white"}>
                <Card.Header><FontAwesomeIcon icon={faList}/> Buildings List</Card.Header>
                <Card.Body>
                    <Table bordered hover striped variant="dark">
                        <thead>
                            <tr>
                            <th>ElementId</th>
                            <th>Name</th>
                            <th>Type</th>
                            <th>Date Created</th>
                            <th>Edit/Delete</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                elements.length === 0 ?
                                <tr align="center">
                                <td colSpan="10">You Dont have Buildings or Apartments Available.</td>
                                </tr> :
                                    elements.map((element) => (
                                <tr key={element.elementId.id}>
                                    <td>{element.elementId.id} </td>
                                    <td>{element.name} </td>
                                    <td>{element.type} </td>
                                    <td>{element.createdTimestamp} </td>
                                    <td>
                                            <ButtonGroup>
                                                <Link to={"edit/Building/"+element.elementId.domain +"/"+element.elementId.id} className="btn btn-sm btn-outline-primary"><FontAwesomeIcon icon={faEdit} /></Link>{' '}
                                                <Button size="sm" variant="outline-danger" ><FontAwesomeIcon icon={faTrash} /></Button>
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
    
