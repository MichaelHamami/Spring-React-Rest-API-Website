import React from "react";
import './App.css';

import {Container , Row , Col} from 'react-bootstrap';
import {BrowserRouter as Router, Switch , Route} from 'react-router-dom';
import NavigatorBar from './components/NavigationBar';
// import Element from './components/Element';
// import ElementList from './components/ElementList';
import Apartment from './components/Apartment';
import ApartmentsList from './components/ApartmentsList';
import Building from './components/Building';
import BuildingsList from './components/BuildingsList';

import Welcome from './components/Welcome';
import Login from './components/Login';
import Register from './components/Register';
import {logout} from './components/logout';
import Profile from './components/Profile';







function AppRouter() {

  return (
    <Router>
      <NavigatorBar/>
      <Container>
        <Row>
          <Col lg={12} className={"margin-top"}>
            <Switch>
              <Route path="/" exact component={Welcome}/>
              <Route path="/login" exact component={Login}/>
              <Route path="/register" exact component={Register}/>
              <Route path="/logout" exact component={logout}/>

              <Route path="/profile/" exact component={Profile}/>
              <Route path="/addApartment" exact component={Apartment}/>
              <Route path="/Apartments" exact component={ApartmentsList}/>
              <Route path="/addBuilding" exact component={Building}/>
              <Route path="/Buildings" exact component={BuildingsList}/>

              <Route path="/edit/Apartment/:elementIdDomain/:elementIdId" exact component={Apartment}/>
              <Route path="/edit/Building/:elementIdDomain/:elementIdId" exact component={Building}/>

            </Switch>
          </Col>
        </Row>
      </Container>
    </Router>

  );
}

export default AppRouter;
