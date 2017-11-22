import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Platform, StyleSheet, Text, View} from "react-native";

export default class Header extends Component {

    render() {
        return (
            <View style={styles.chatHeader}>
                <Text style={styles.chatHeaderTitle}>{this.props.title}</Text>
            </View>
        );
    }
}

Header.propTypes = {
    title: PropTypes.string
};

const styles = StyleSheet.create({
    chatHeader: {
        backgroundColor: '#3c8dbc',
        alignItems: 'center',
        justifyContent: 'center',
        ...Platform.select({
            ios: {
                shadowColor: '#000000',
                shadowOffset: {width: 0, height: 1},
                shadowOpacity: 0.4,
                shadowRadius: 3,
                height: 64,
                paddingTop: 20
            },
            android: {
                elevation: 5,
                height: 54
            },
        })
    },
    chatHeaderTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: 'white'
    }
});