import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Image, StyleSheet, Text, View} from "react-native";
import Icon from 'react-native-vector-icons/FontAwesome';

export default class MessageView extends Component {

    render() {

        const trimDate = (value) => {
            return value.slice(0, value.length - 7).replace('\n', ' ')
        };

        if (this.props.pullRight) {
            return (
                <View style={styles.messageViewContainer}>
                    <View style={styles.messageViewTextContainer}>
                        <Text style={styles.messageViewDateRight}>{trimDate(this.props.date)}</Text>
                        <View style={styles.messageViewTextRight}>
                            <Text style={{color: 'white'}}>{this.props.message}</Text>
                        </View>
                    </View>
                    {
                        this.props.imageUri ?
                            <Image source={{uri: this.props.imageUri}}
                                   style={styles.messageViewImage}/> :
                            <Icon name={this.props.icon} color='#3c8dbc' size={50} style={styles.messageViewIcon}/>
                    }
                </View>
            )
        } else {
            return (
                <View style={styles.messageViewContainer}>
                    {
                        this.props.imageUri ?
                            <Image source={{uri: this.props.imageUri}}
                                   style={styles.messageViewImage}/> :
                            <Icon name={this.props.icon} color='#3c8dbc' size={50} style={styles.messageViewIcon}/>
                    }
                    <View style={styles.messageViewTextContainer}>
                        <Text style={styles.messageViewDateLeft}>{trimDate(this.props.date)}</Text>
                        <View style={styles.messageViewTextLeft}>
                            <Text style={{color: 'black'}}>{this.props.message}</Text>
                        </View>
                    </View>
                </View>
            )
        }

    }

}

MessageView.defaultProps = {
    pullRight: true,
    imageUri: '',
    icon: 'user-circle',
    date: '',
    message: ''
};

MessageView.propTypes = {
    pullRight: PropTypes.bool,
    imageUri: PropTypes.string,
    icon: PropTypes.string, // FontAwesome icon name
    date: PropTypes.string,
    message: PropTypes.string
};

const styles = StyleSheet.create({
    messageViewContainer: {
        marginBottom: 10,
        flexDirection: 'row',
    },
    messageViewImage: {
        height: 50,
        width: 50,
        borderRadius: 25,
    },
    messageViewTextContainer: {
        flex: 1,
        paddingLeft: 5,
        paddingRight: 5
    },
    messageViewDateRight: {
        fontSize: 12,
        color: 'gray',
        textAlign: 'left'
    },
    messageViewDateLeft: {
        fontSize: 12,
        color: 'gray',
        textAlign: 'right'
    },
    messageViewTextRight: {
        backgroundColor: '#3c8dbc',
        borderRadius: 15,
        overflow: 'hidden',
        paddingRight: 10,
        paddingLeft: 10,
        paddingTop: 5,
        paddingBottom: 5
    },
    messageViewTextLeft: {
        backgroundColor: 'lightgray',
        borderRadius: 15,
        overflow: 'hidden',
        paddingRight: 10,
        paddingLeft: 10,
        paddingTop: 5,
        paddingBottom: 5
    },
});