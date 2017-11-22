import React, { Component } from 'react';
import { StatusBar, StyleSheet, View } from 'react-native';
import PropTypes from 'prop-types';
import { CommunicationService } from "../../services/communicationService";
import Header from "./Header";
import Footer from "./Footer";
import Contacts from "./contacts/Contacts";
import Dialogs from "./dialogs/Dialogs";
import Settings from "./settings/Settings";

export default class Chat extends Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedPage: this.props.selectedPage,
        };
        this.pages = [
            {
                name: 'Contacts',
                icon: 'users'
            },
            {
                name: 'Dialogs',
                icon: 'comments'
            },
            {
                name: 'Settings',
                icon: 'cogs'
            }
        ];
    }

    onFooterPress = (position) => {
        this.setState({selectedPage: position})
    };

    render() {
        const getPageView = (index) => {
            switch (index) {
                case 0:
                    return (
                        <View style={styles.main}>
                            <Contacts
                                communicationService={this.props.communicationService}
                                navigation={this.props.navigation}
                            />
                        </View>
                    );
                case 1:
                    return (
                        <View style={styles.main}>
                            <Dialogs
                                communicationService={this.props.communicationService}
                                navigation={this.props.navigation}
                            />
                        </View>
                    );
                case 2:
                    return (
                        <View style={styles.main}>
                            <Settings
                                onLogoutPress={this.props.onLogoutPress}
                                communicationService={this.props.communicationService}
                                navigation={this.props.navigation}
                            />
                        </View>
                    );
                default:
                    return (
                        <View style={styles.main}>
                            <Contacts
                                communicationService={this.props.communicationService}
                                navigation={this.props.navigation}
                            />
                        </View>
                    );
            }
        };

        return (
            <View style={styles.container}>
                <StatusBar
                    backgroundColor="#3c8dbc"
                    barStyle="light-content"
                />
                <Header title={this.pages[this.state.selectedPage].name}/>
                {getPageView(this.state.selectedPage)}
                <Footer
                    buttonNames={this.pages.map(item => item.icon)}
                    onButtonPress={this.onFooterPress}
                    position={this.state.selectedPage}
                />
            </View>
        )
    }
}

Chat.defaultProps = {
    selectedPage: 0
};

Chat.propTypes = {
    onLogoutPress: PropTypes.func.isRequired,
    communicationService: PropTypes.instanceOf(CommunicationService).isRequired,
    selectedPage: PropTypes.number
};

const styles = StyleSheet.create({
    container: {
        flex: 1
    },
    main: {
        flex: 1,
        backgroundColor: 'white'
    }
});