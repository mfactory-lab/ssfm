import React, {Component} from 'react';
import {FlatList, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {CommunicationService} from "../../../services/communicationService";
import ChatListItem from "../ChatListItem";

export default class Dialogs extends Component {

    newDialogsSubscription;
    newMessagesSubscription;

    constructor(props) {
        super(props);
        this.state = {
            dialogs: [],
        };
    }

    componentWillMount() {
        this.newDialogsSubscription = this.props.communicationService.newDialog().subscribe(dialog => {
            this.updateDialogs();
        });
        this.newMessagesSubscription = this.props.communicationService.newMessage().subscribe(({dialogReference, messageView}) => {
            this.updateDialogs();
        });
    }

    componentDidMount() {
        this.updateDialogs();
    }

    componentWillUnmount() {
        this.newDialogsSubscription.unsubscribe();
        this.newMessagesSubscription.unsubscribe();
    }

    updateDialogs() {
        let dialogs = this.props.communicationService.listDialogs();
        this.setState({
            dialogs: dialogs
        })
    }

    onPress(dialog) {
        this.props.navigation.navigate(
            'Dialog',
            {
                dialogReference: dialog.dialogReference,
                communicationService: this.props.communicationService,
                headerTitle: dialog.name
            }
        );
    }

    renderItem = ({item: dialog}) => (
        <TouchableOpacity
            onPress={() => this.onPress(dialog)}
            disabled={false}
            activeOpacity={0.8}>
            <ChatListItem
                name={dialog.name}
                imageUri={dialog.avatar}
                date={dialog.lastMessageDate}
                message={dialog.lastMessage}/>
        </TouchableOpacity>
    );

    keyExtractor = (dialog, index) => dialog.dialogReference.id;

    render() {
        return (
            <FlatList
                data={this.state.dialogs}
                renderItem={this.renderItem}
                keyExtractor={this.keyExtractor}
            />
        )
    }
}

Dialogs.propTypes = {
    communicationService: PropTypes.instanceOf(CommunicationService).isRequired
};