import React, {Component} from 'react';
import {Navbar, Nav} from 'react-bootstrap';
import {Link} from 'react-router-dom';

export default class NavigatorBar extends Component {

    
    render() {
        
        let user = localStorage.getItem('user');
        // let username = localStorage.getItem('username');

        return (
            <Navbar bg="dark" variant="dark">
                <Link to={""} className="navbar-brand">
                </Link>
                <Nav className="mr-auto">
                <Link to={""} className="nav-link">Welcome </Link>
                {user === null ?
                  
                  <Link to={"login"} className="nav-link">Login</Link>

                 : 
                
                 (<Link to={"logout"} className="nav-link">Logout</Link>)
                
                }
                <Link to={"addApartment"} className="nav-link">Add Apartment </Link>
                <Link to={"Apartments"} className="nav-link">Apartments List </Link>
                <Link to={"addBuilding"} className="nav-link">Add Building </Link>
                <Link to={"Buildings"} className="nav-link">Buildings List </Link>
                <Link to={"register"} className="nav-link">Register</Link>

                {/* <Link to={"addElement"} className="nav-link">Add Element </Link> */}
                {/* <Link to={"Elements"} className="nav-link">Element List </Link> */}

                </Nav>
            </Navbar>
        );
    }
}