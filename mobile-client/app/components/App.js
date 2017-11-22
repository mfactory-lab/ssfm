import React, { Component } from 'react';
import { ActivityIndicator, AppRegistry, ImageBackground } from 'react-native';
import { StackNavigator } from 'react-navigation';
import Login from "./Login";
import Chat from "./chat/Chat";
import Dialog from "./chat/dialog/Dialog";
import About from "./chat/about/About";

import {CommunicationService} from "../../app/services/communicationService";
import { AuthService } from "../services/authService";

class MainScreen extends Component {

    static navigationOptions = {
        header: null
    };

    constructor() {
        super();

        console.ignoredYellowBox = [
            'Setting a timer'
        ];

        this.state = {
            isAuthenticated: false,
            isLoaded: false
        };

        this.authService = new AuthService();
        this.communicationService = new CommunicationService();
    }

    componentDidMount() {
        this.authService.login()
            .then(
                contactInfo => {
                    this.setState({ isAuthenticated: true, isLoaded: true })
                },
                reason => {
                    console.log(reason);
                    this.setState({ isAuthenticated: false, isLoaded: true });
                }
            )
            .catch((error) => {
                console.error(error);
                this.setState({ isLoaded: true });
            });
    }

    renderScreen() {
        if (this.state.isAuthenticated)
            return <Chat
                onLogoutPress={() => this.authService.logout().then(() => this.setState({isAuthenticated: false}))}
                communicationService={this.communicationService}
                navigation={this.props.navigation}
            />;
        else
            return <Login
                onLoginPress={() => this.setState({isAuthenticated: true})}
                communicationService={this.communicationService}
            />;
    }

    render() {
        if (this.state.isLoaded) {
            return this.renderScreen()
        } else {
          return (
              <ImageBackground
                  source={require('../images/background.png')}
                  style={{ flex: 1, width: null, height: null, justifyContent: 'center' }}>
                  <ActivityIndicator color='#3c8dbc' size='large' style={{ flex: 1 }} />
              </ImageBackground>
          )
        }
    }
}

const App = StackNavigator({
    MainScreen: {screen: MainScreen},
    Dialog: {screen: Dialog},
    About: {screen: About}
});

AppRegistry.registerComponent('ssfm', () => App);