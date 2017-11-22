import React, {Component} from 'react';
import {ScrollView, StyleSheet, Text, View} from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';

export default class About extends Component {

    static navigationOptions = {
        title: 'About',
        // headerRight: <Icon name='info-circle' color='white' size={40} style={{marginRight: 10}}/>,
        headerStyle: {backgroundColor: '#3c8dbc'},
        headerTintColor: 'white',
    };

    render() {
        return (
            <View style={{flex: 1}}>
                <ScrollView style={styles.aboutContainer}>
                    <Text style={styles.aboutHeaderText}>
                        <Text style={{fontWeight: 'bold'}}>ssfm</Text> is a simple small fancy messenger
                    </Text>
                    <View style={styles.aboutRow}>
                        <Text style={styles.aboutRowFirstCol}>Version</Text>
                        <Text style={styles.aboutRowSecondCol}>0.0.1</Text>
                    </View>
                    <View style={styles.aboutRow}>
                        <Text style={styles.aboutRowFirstCol}>Authors</Text>
                        <Text style={styles.aboutRowSecondCol} numberOfLines={2}>Alexander Ray (email@domain.com)</Text>
                    </View>
                    <View style={styles.aboutRow}>
                        <Text style={styles.aboutRowFirstCol}> </Text>
                        <Text style={styles.aboutRowSecondCol} numberOfLines={2}>Maksim Budilovskiy (email@domain.com)</Text>
                    </View>
                </ScrollView>
                <View style={styles.aboutFooter}>
                    <Text style={styles.aboutFooterFirstCol}>Copyright © 2017, Alexander Ray</Text>
                    <Text style={styles.aboutFooterSecondCol}>All rights reserved</Text>
                </View>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    aboutContainer: {
        flex: 1,
        paddingLeft: 10,
        paddingRight: 10,
        backgroundColor: 'white'
    },
    aboutHeaderText: {
        paddingTop: 20,
        paddingBottom: 20,
        textAlign: 'center',
        fontSize: 16,
        color: 'black'
    },
    aboutRow: {
        flexDirection: 'row',
        height: 50,
        alignItems: 'center',
        borderColor: '#dddddd',
        borderBottomWidth: 1,
    },
    aboutRowFirstCol: {
        flex: 1,
        fontWeight: 'bold',
        color: 'black'
    },
    aboutRowSecondCol: {
        flex: 1,
        textAlign: 'right'
    },
    aboutFooter: {
        height: 60,
        borderTopWidth: 1,
        borderColor: '#cccccc',
        backgroundColor: '#eeeeee',
        justifyContent: 'center',
        padding: 10
    },
    aboutFooterFirstCol: {
        textAlign: 'center',
        fontWeight: 'bold',
        color: 'black'
    },
    aboutFooterSecondCol: {
        textAlign: 'center'
    }
});