import config from '../../app.config'
import io from "socket.io-client";
import {ModelService} from "./modelService";
import * as Rx from "rxjs";

window.navigator.userAgent = 'react-native';

const scalaJS = require('./scalaJS/scala-js-fastopt.js');

let instance = null;

// Singleton
export class CommunicationService {

    modelService;
    contactsStatusChangeSubject;
    newMessagesSubject;

    constructor() {
        this.socket = io(config.proxyHost + ":" + config.proxyPort);
        this.transportService = new scalaJS.TransportService(this.socket);

        if (!instance) {
            instance = this;
        }
        return instance;
    }

    connected() {
        return this.socket && this.socket.connected;
    }

    contactsUpdated() {
        return this.modelService.contactsUpdatedSubject
    }

    newMessage() {
        return this.modelService.newMessagesSubject
    }

    newDialog() {
        return this.modelService.newDialogSubject
    }

    initModel(contactInfo) {
        this.modelService = new ModelService();
        // set user
        this.modelService.setContactInfo(contactInfo);
        this.listen();
        // set contacts
        return this.transportService.listContacts().then(contacts => {
            this.modelService.setContacts(contacts);
            // set dialogs
            return this.transportService.listDialogs(contactInfo.contactReference)
                .then(dialogs => {
                    this.modelService.setDialogs(dialogs);
                    // set messages
                    dialogs.forEach(dialogInfo => {
                        return this.transportService.listMessages(dialogInfo.dialogReference)
                            .then(messages => this.modelService.setMessages(dialogInfo.dialogReference, messages))
                    });
                })
                .then(() => this.modelService.getContactInfo());
        })
    }

    listen() {
        this.contactsStatusChangeSubject = new Rx.Subject();
        this.transportService.listenNewUsers(this.contactsStatusChangeSubject);
        this.contactsStatusChangeSubject.subscribe(statusChangedEvent => {
            console.log('this.contactsStatusChangeSubject', statusChangedEvent);
            if (statusChangedEvent.online) {
                this.modelService.addContact(statusChangedEvent.contact);
            } else {
                this.modelService.removeContact(statusChangedEvent.contact)
            }
        });

        this.newMessagesSubject = new Rx.Subject();
        this.transportService.listenNewMessages(this.getUser().contactReference, this.newMessagesSubject);
        this.newMessagesSubject.subscribe(message => {
            this.modelService.addMessage(message);
        })
    }

    login(userInfo) {
        return this.transportService.addUser(userInfo).then(contactInfo => {
            this.transportService.sendConnect(contactInfo.contactReference);
            if (contactInfo.name === userInfo.name && contactInfo.avatar === userInfo.avatarUrl) {
                return this.initModel(contactInfo).then(contactInfo => {
                    return contactInfo;
                });
            }
        });
    }

    logout() {
        this.transportService.logout(this.getUser());
        this.contactsStatusChangeSubject.complete();
        this.newMessagesSubject.complete();
        this.modelService = null;
    }

    getUser() {
        return this.modelService.getContactInfo();
    }

    listContacts() {
        return this.modelService.getContacts();
    }

    listDialogs() {
        return this.modelService.getDialogs();
    }

    getDialog(dialogReference) {
        return this.modelService.getDialog(dialogReference);
    }

    isContactOnline(contactReference) {
        return this.modelService.isContactOnline(contactReference);
    }

    processDialog(contactReference) {
        return this.transportService.getOrCreateDialog(this.modelService.getContactInfo().contactReference, contactReference)
            .then(dialogInfo => {
                this.modelService.addDialog(dialogInfo);
                return dialogInfo.dialogReference;
            })
    }

    getMessages(dialogReference) {
        return this.modelService.getMessages(dialogReference);
    }

    sendMessage(dialogReference, value) {
        let message = {
            dialogReference: dialogReference,
            contactInfo: this.modelService.getContactInfo(),
            value: value,
            date: ""
        };
        this.transportService.sendMessage(message)
    }

}