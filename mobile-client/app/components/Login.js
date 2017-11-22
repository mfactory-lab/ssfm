import React, { Component } from 'react';
import {
    ActivityIndicator,
    Alert,
    ImageBackground,
    StatusBar,
    StyleSheet,
    Text,
    TouchableOpacity,
    View
} from 'react-native';
import PropTypes from 'prop-types';
import Auth0 from 'react-native-auth0';
import config from '../../app.config'
import { CommunicationService } from "../services/communicationService";
import { AuthService } from "../services/authService";

const auth0 = new Auth0({
    domain: config.auth0domain,
    clientId: config.auth0clientId
});

export default class Login extends Component {

    static navigationOptions = {
        title: 'Login',
    };

    constructor() {
        super();
        this.authService = new AuthService();
        this.state = {
            loggedIn: false
        }
    }

    login() {
        if (!this.props.communicationService.connected) {
            Alert.alert(
                'Connection error',
                'Check your connection.\n{dev-hint: run NodeJS-proxy and check host:port in "app.config.js"}',
                [{text: 'OK', onPress: () => {}}],
                {cancelable: false}
            );
        } else {
            this.setState({ loggedIn: true });
            this.authService.authenticate()
                .then(
                    result => this.props.onLoginPress(),
                    reason => {
                        console.log(reason);
                        this.setState({ loggedIn: false });
                    }
                )
                .catch(error => {
                    console.log(error);
                    this.setState({ loggedIn: false });
                });
        }
    }

    render() {
        return (
            <ImageBackground source={require('../images/background.png')} style={styles.background}>
                <StatusBar
                    backgroundColor="transparent"
                    translucent={true}
                    barStyle="light-content"
                />
                <View style={styles.titleContent}>
                    <Text style={styles.titleApp}>ssfm</Text>
                </View>
                {
                    this.state.loggedIn ?
                        <ActivityIndicator
                            style={styles.indicator}
                            animating={true}
                            size='large'
                            color='#3c8dbc'
                        /> :
                        <TouchableOpacity
                            style={styles.buttonContainer}
                            onPress={this.login.bind(this)}
                            disabled={this.state.loggedIn}
                            activeOpacity={0.8}>
                            <Text style={styles.loginButton}>LOGIN</Text>
                        </TouchableOpacity>
                }
            </ImageBackground>
        )
    }
}

Login.propTypes = {
    onLoginPress: PropTypes.func.isRequired,
    communicationService: PropTypes.instanceOf(CommunicationService).isRequired
};

const styles = StyleSheet.create({
    background: {
        flex: 1,
        width: null,
        height: null,
        justifyContent: 'center'
    },
    titleContent: {
        flexGrow: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: 'transparent'
    },
    titleApp: {
        fontSize: 60,
        fontWeight: 'bold',
        textAlign: 'center',
        color: '#ffffff'
    },
    buttonContainer: {
        marginBottom: 70,
        alignItems: 'center'
    },
    loginButton: {
        fontSize: 16,
        color: 'white',
        textAlign: 'center',
        backgroundColor: '#3c8dbc',
        paddingVertical: 12,
        width: 160
    },
    indicator: {
        marginBottom: 76
    }
});