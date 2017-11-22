import React, {Component} from 'react';
import {FlatList, TouchableOpacity} from "react-native";
import PropTypes from 'prop-types';
import {CommunicationService} from "../../../services/communicationService";
import ChatListItem from "../ChatListItem";

export default class Contacts extends Component {

    contactsUpdateSubscription;
    newMessagesSubscription;

    constructor(props) {
        super(props);
        this.state = {
            contacts: [],
        };
    }

    componentWillMount() {
        this.contactsUpdateSubscription = this.props.communicationService.contactsUpdated().subscribe(contactReference => {
            this.updateContacts();
        });
        this.newMessagesSubscription = this.props.communicationService.newMessage().subscribe(({dialogReference, messageView}) => {
            this.updateContacts();
        });
    }

    componentDidMount() {
        this.updateContacts();
    }

    componentWillUnmount() {
        this.contactsUpdateSubscription.unsubscribe();
        this.newMessagesSubscription.unsubscribe();
    }

    updateContacts() {
        let contacts = this.props.communicationService.listContacts();
        this.setState({
            contacts: contacts
        });
    }

    onPress(contact) {
        this.props.communicationService.processDialog(contact.contactReference).then(dialogReference => {
            this.props.navigation.navigate(
                'Dialog',
                {
                    dialogReference: dialogReference,
                    communicationService: this.props.communicationService,
                    headerTitle: contact.name
                }
            );
        });
    }

    renderItem = ({item: contact}) => (
        <TouchableOpacity
            onPress={() => this.onPress(contact)}
            disabled={false}
            activeOpacity={0.8}>
            <ChatListItem
                name={contact.name}
                imageUri={contact.avatar}
                date={contact.lastMessageDate}
                message={contact.lastMessage}/>
        </TouchableOpacity>
    );

    keyExtractor = (contact, index) => contact.contactReference.id;

    render() {
        return (
            <FlatList
                data={this.state.contacts}
                renderItem={this.renderItem}
                keyExtractor={this.keyExtractor}
            />
        )
    }
}

Contacts.propTypes = {
    communicationService: PropTypes.instanceOf(CommunicationService).isRequired
};