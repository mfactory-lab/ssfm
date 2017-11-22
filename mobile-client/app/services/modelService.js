import * as Rx from "rxjs";

export class ModelService {

    contactInfo;

    contactsUpdatedSubject;
    newDialogSubject;
    newMessagesSubject;

    contactViews; // Map<string, ContactView>;
    dialogViews; // Map<string, DialogView>;
    dialogs; // Map<string, MessageView[]>;

    constructor() {
        this.contactViews = new Map();
        this.dialogViews = new Map();
        this.dialogs = new Map();

        this.contactsUpdatedSubject = new Rx.Subject();
        this.newDialogSubject = new Rx.Subject();
        this.newMessagesSubject = new Rx.Subject();
    }

    getContactView(contactInfo) {
        return {
            contactReference: contactInfo.contactReference,
            name: contactInfo.name,
            avatar: contactInfo.avatar,
            lastMessage: "",
            lastMessageDate: ""
        }
    }

    getDialogView(dialogInfo) {
        let interlocutor = dialogInfo.users.find(user => user.contactReference.id !== this.contactInfo.contactReference.id);
        return {
            dialogReference: dialogInfo.dialogReference,
            contactReference: interlocutor.contactReference,
            name: interlocutor.name,
            avatar: interlocutor.avatar,
            lastMessage: dialogInfo.lastMessage,
            lastMessageDate: dialogInfo.lastMessageDate
        }
    }

    getMessageView(message) {
        return {
            contactReference: message.contactInfo.contactReference,
            contactName: message.contactInfo.name,
            contactAvatar: message.contactInfo.avatar,
            message: message.value,
            date: message.date
        }
    }

    setContactLastMessage(contactInfo, contactView) {
        this.dialogViews.forEach(value => {
            if (value.contactReference.id === contactInfo.contactReference.id) {
                contactView.lastMessage = value.lastMessage;
                contactView.lastMessageDate = value.lastMessageDate;
            }
        });
    }

    setContactInfo(contactInfo) {
        this.contactInfo = contactInfo;
        return this.contactInfo;
    }

    getContactInfo() {
        return this.contactInfo;
    }

    setContacts(contacts) {
        contacts.map(info => this.addContact(info));
    }

    getContacts() {
        return Array.from(this.contactViews.values())
            .filter(contact => contact.contactReference.id !== this.contactInfo.contactReference.id)
            .sort((a, b) => a.name.localeCompare(b.name));
    }

    addContact(contactInfo) {
        let contactView = this.getContactView(contactInfo);
        this.setContactLastMessage(contactInfo, contactView);
        this.contactViews.set(contactInfo.contactReference.id, contactView);
        this.contactsUpdatedSubject.next(contactInfo.contactReference);
    }

    removeContact(contactInfo) {
        if (this.contactViews.has(contactInfo.contactReference.id)) {
            this.contactViews.delete(contactInfo.contactReference.id);
            this.contactsUpdatedSubject.next(contactInfo.contactReference);
        }
    }

    isContactOnline(contactReference) {
        return this.contactViews.has(contactReference.id)
    }

    setDialogs(dialogs) {
        dialogs.map(info => this.addDialog(info));
        this.setContactsLastMessages(dialogs);
    }

    getDialogs() {
        return Array.from(this.dialogViews.values())
            .sort((a, b) => b.lastMessageDate.localeCompare(a.lastMessageDate));
    }

    addDialog(dialogInfo) {
        this.dialogViews.set(dialogInfo.dialogReference.id, this.getDialogView(dialogInfo));
        if (!this.dialogs.has(dialogInfo.dialogReference.id)) {
            this.dialogs.set(dialogInfo.dialogReference.id, []);
        }
    }

    getDialog(dialogReference) {
        return this.dialogViews.get(dialogReference.id);
    }

    setContactsLastMessages(dialogs) {
        dialogs.forEach(dialog => {
            let interlocutor = dialog.users.find(user => user.contactReference.id !== this.contactInfo.contactReference.id);
            if (this.contactViews.has(interlocutor.contactReference.id)) {
                this.contactViews.get(interlocutor.contactReference.id).lastMessage = dialog.lastMessage;
                this.contactViews.get(interlocutor.contactReference.id).lastMessageDate = dialog.lastMessageDate;
            }
        })
    }

    setMessages(dialogReference, messages) {
        this.dialogs.set(dialogReference.id, messages.map(message => this.getMessageView(message)));
    }

    getMessages(dialogReference) {
        return Array.from(this.dialogs.get(dialogReference.id).sort((a, b) => b.date.localeCompare(a.date)));
    }

    addMessage(message) {
        let messageView = this.getMessageView(message);
        if (this.dialogs.has(message.dialogReference.id)) {
            this.dialogs.get(message.dialogReference.id).push(messageView);
            this.dialogViews.get(message.dialogReference.id).lastMessage = message.value;
            this.dialogViews.get(message.dialogReference.id).lastMessageDate = message.date;
        } else {
            this.dialogs.set(message.dialogReference.id, [messageView]);
            let newDialogView = {
                dialogReference: message.dialogReference,
                contactReference: message.contactInfo.contactReference,
                name: message.contactInfo.name,
                avatar: message.contactInfo.avatar,
                lastMessage: message.value,
                lastMessageDate: message.date
            };
            this.dialogViews.set(newDialogView.dialogReference.id, newDialogView);
            this.newDialogSubject.next(newDialogView);
        }
        let contactReference = this.dialogViews.get(message.dialogReference.id).contactReference;
        if (this.contactViews.has(contactReference.id)) {
            this.contactViews.get(contactReference.id).lastMessage = message.value;
            this.contactViews.get(contactReference.id).lastMessageDate = message.date;
        }
        this.newMessagesSubject.next({dialogReference: message.dialogReference, messageView: messageView});
    }

}