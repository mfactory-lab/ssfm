import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Image, StyleSheet, Text, View} from "react-native";
import Icon from 'react-native-vector-icons/FontAwesome';

export default class ChatListItem extends Component {

    render() {

        const trimDate = (value) => {
            return value.slice(0, value.length - 7)
        };

        return (
            <View style={styles.chatListItemContainer}>
                <View style={styles.chatListItemImageContainer}>
                    {
                        this.props.imageUri ?
                            <Image source={{uri: this.props.imageUri}} style={styles.chatListItemAvatarImage}/> :
                            <Icon name={this.props.icon} style={styles.chatListItemAvatarImage} color='#3c8dbc' size={50}/>
                    }
                </View>
                <View style={styles.chatListItemInfoContainer}>
                    <View style={styles.chatListItemInfoFirstRow}>
                        <Text style={styles.chatListItemName} numberOfLines={1}>{this.props.name}</Text>
                        <Text style={styles.chatListItemDate} numberOfLines={2}>{trimDate(this.props.date)}</Text>
                    </View>
                    <View style={styles.chatListItemInfoSecondRow}>
                        <Text style={styles.chatListItemMessage} numberOfLines={1}>{this.props.message}</Text>
                    </View>
                </View>
            </View>
        )
    }
}

ChatListItem.defaultProps = {
    name: '',
    imageUri: '',
    icon: 'user-circle',
    date: '',
    message: ''
};

ChatListItem.propTypes = {
    name: PropTypes.string,
    imageUri: PropTypes.string,
    icon: PropTypes.string, // FontAwesome icon name
    date: PropTypes.string,
    message: PropTypes.string
};

const styles = StyleSheet.create({
    chatListItemContainer: {
        flexDirection: 'row',
        borderTopColor: "#dddddd",
        borderTopWidth: 1,
        height: 70
    },
    chatListItemImageContainer: {
        justifyContent: 'center'
    },
    chatListItemAvatarImage: {
        borderRadius: 25,
        height: 50,
        width: 50,
        marginLeft: 10,
        marginRight: 10
    },
    chatListItemInfoContainer: {
        flex: 1,
        flexDirection: 'column',
        marginRight: 10
    },
    chatListItemInfoFirstRow: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        paddingTop: 5
    },
    chatListItemInfoSecondRow: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'flex-start',
        paddingTop: 5
    },
    chatListItemName: {
        flex: 1,
        color: 'black',
        fontSize: 16,
        fontWeight: 'bold'
    },
    chatListItemDate: {
        width: 80,
        fontSize: 12,
        color: '#999999',
        textAlign: 'right'
    },
    chatListItemMessage: {
        color: '#666666'
    }
});