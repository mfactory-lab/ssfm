import React, {Component} from 'react';
import {FlatList, Platform, StyleSheet, TextInput, TouchableOpacity, View} from 'react-native';
import Icon from 'react-native-vector-icons/FontAwesome';
import KeyboardSpacer from 'react-native-keyboard-spacer';
import MessageView from "./MessageView";

export default class Dialog extends Component {

    communicationService;
    dialogView;
    me;
    isInterlocutorOnline;
    contactsUpdateSubscription;
    newMessagesSubscription;

    static navigationOptions = ({navigation}) => ({
        title: navigation.state.params.headerTitle,
        headerRight: navigation.state.params.headerRightImage,
        headerStyle: {backgroundColor: '#3c8dbc'},
        headerTintColor: 'white',
    });

    constructor(props) {
        super(props);

        this.communicationService = this.props.navigation.state.params.communicationService;
        this.dialogView = this.communicationService.getDialog(this.props.navigation.state.params.dialogReference);
        this.me = this.communicationService.getUser();
        this.isInterlocutorOnline = this.communicationService.isContactOnline(this.dialogView.contactReference);

        this.state = {
            messages: [],
            message: ''
        }
    }

    componentWillMount() {
        this.props.navigation.setParams({
            headerRightImage: <View
                style={this.isInterlocutorOnline ? styles.headerRightImageOnline : styles.headerRightImageOffline}/>
        });

        this.contactsUpdateSubscription = this.communicationService.contactsUpdated().subscribe(contactReference => {
            this.isInterlocutorOnline = this.communicationService.isContactOnline(this.dialogView.contactReference);
            this.props.navigation.setParams({
                headerRightImage: <View
                    style={this.isInterlocutorOnline ? styles.headerRightImageOnline : styles.headerRightImageOffline}/>
            });
        });

        this.newMessagesSubscription = this.communicationService.newMessage().subscribe(({dialogReference, messageView}) => {
            if (dialogReference.id === this.dialogView.dialogReference.id) {
                this.setState({messages: [messageView].concat(this.state.messages)})
            }
        });
    }

    componentDidMount() {
        this.updateDialog();
    }

    componentWillUnmount() {
        this.contactsUpdateSubscription.unsubscribe();
        this.newMessagesSubscription.unsubscribe();
    }

    updateDialog() {
        let messages = this.communicationService.getMessages(this.dialogView.dialogReference);
        this.setState({
            messages: messages
        })
    }

    onSendPress() {
        this.communicationService.sendMessage(this.dialogView.dialogReference, this.state.message);
        this.setState({message: ''});
    }

    renderItem = ({item: message}) => (
        <MessageView
            pullRight={message.contactReference.id === this.me.contactReference.id}
            imageUri={message.contactAvatar}
            message={message.message}
            date={message.date}/>
    );

    keyExtractor = (message, index) => message.date.concat(index);

    render() {
        return (
            <View style={{flex: 1}}>
                <FlatList
                    inverted
                    style={styles.messagesContainer}
                    data={this.state.messages}
                    keyExtractor={this.keyExtractor}
                    renderItem={this.renderItem}
                    ListFooterComponent={<View style={{height: 15}}/>}
                />
                <View style={styles.dialogFooter}>
                    <View style={styles.dialogInputContainer}>
                        <TextInput
                            style={styles.dialogInput}
                            placeholder='Type message...'
                            multiline={true}
                            onChangeText={(value) => this.setState({message: value})}
                            value={this.state.message}/>
                    </View>
                    <View style={styles.dialogSendBtnContainer}>
                        <TouchableOpacity
                            onPress={this.onSendPress.bind(this)}
                            style={styles.dialogSendBtn}
                            disabled={this.state.message === ''}
                            activeOpacity={0.8}>
                            <Icon name='arrow-right' size={20} color='white'/>
                        </TouchableOpacity>
                    </View>
                </View>
                {
                    Platform.OS.match('ios') ? <KeyboardSpacer/> : <View/>
                }
            </View>
        )
    }
}

const styles = StyleSheet.create({
    headerRightImageOnline: {
        width: 16,
        height: 16,
        borderRadius: 8,
        marginRight: 10,
        backgroundColor: '#00a65a',
        borderWidth: 1,
        borderColor: 'white'
    },
    headerRightImageOffline: {
        width: 16,
        height: 16,
        borderRadius: 8,
        marginRight: 10,
        backgroundColor: 'red',
        borderWidth: 1,
        borderColor: 'white'
    },
    messagesContainer: {
        flex: 1,
        backgroundColor: '#eeeeee',
        padding: 5,
    },
    dialogFooter: {
        flexDirection: 'row',
        height: 60,
        backgroundColor: 'white',
    },
    dialogInputContainer: {
        flex: 1,
        justifyContent: 'center',
    },
    dialogInput: {
        marginLeft: 8,
        marginRight: 8
    },
    dialogSendBtnContainer: {
        justifyContent: 'center',
    },
    dialogSendBtn: {
        justifyContent: 'center',
        alignItems: 'center',
        width: 40,
        height: 40,
        marginRight: 10,
        backgroundColor: '#3c8dbc',
        borderRadius: 20
    }
});