import React, { Component } from 'react';
import { Image, ImageBackground, ScrollView, StyleSheet, Text, TouchableOpacity } from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';
import PropTypes from 'prop-types';
import { CommunicationService } from "../../../services/communicationService";
import ChatListItem from "../ChatListItem";

export default class Settings extends Component {

    onLogoutPress = () => {
        this.props.onLogoutPress();
    };

    onAboutPress = () => {
        const {navigate} = this.props.navigation;
        navigate('About');
    };

    render() {

        let user = this.props.communicationService.getUser();

        return (
            <ScrollView>
                <ImageBackground source={require('../../../images/background.png')} style={styles.background}>
                    {
                        user.avatar ?
                            <Image source={{uri: user.avatar}} style={styles.settingsUserImage}/> :
                            <Icon name='user-circle' style={styles.settingsUserImage} color='white' size={70}/>
                    }
                    <Text style={styles.settingUserName}>{user.name}</Text>
                </ImageBackground>
                <TouchableOpacity
                    onPress={this.onLogoutPress}
                    disabled={false}
                    activeOpacity={0.8}>
                    <ChatListItem
                        name="Log out"
                        icon='sign-out'
                        message="Exit from application"/>
                </TouchableOpacity>
                <TouchableOpacity
                    onPress={this.onAboutPress}
                    disabled={false}
                    activeOpacity={0.8}>
                    <ChatListItem
                        name="About"
                        icon='info-circle'
                        message="About ssfm"/>
                </TouchableOpacity>
            </ScrollView>
        )
    }
}

Settings.propTypes = {
    communicationService: PropTypes.instanceOf(CommunicationService).isRequired,
    onLogoutPress: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
    background: {
        height: 160,
        justifyContent: 'center',
        alignItems: 'center'
    },
    settingsUserImage: {
        width: 70,
        height: 70,
        borderRadius: 35,
        margin: 5,
        borderWidth: 2,
        borderColor: '#d2d6de'
    },
    settingUserName: {
        textAlign: 'center',
        color: 'white',
        fontSize: 16,
        backgroundColor: 'transparent'
    }
});