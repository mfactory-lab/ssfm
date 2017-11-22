import {Injectable} from '@angular/core';
import {
  ContactInfo,
  ContactReference,
  ContactView,
  DialogInfo,
  DialogReference,
  DialogView,
  Message,
  MessageView
} from "../model/model";
import {Subject} from "rxjs/Subject";

@Injectable()
export class ModelService {

  contactInfo: ContactInfo;

  contactViews: Map<string, ContactView>;
  dialogViews: Map<string, DialogView>;
  dialogs: Map<string, MessageView[]>;

  addContactSubject: Subject<ContactView>;
  removeContactSubject: Subject<ContactReference>;
  newDialogSubject: Subject<DialogView>;

  constructor() {
    this.contactViews = new Map<string, ContactView>();
    this.dialogViews = new Map<string, DialogView>();
    this.dialogs = new Map<string, MessageView[]>();

    this.addContactSubject = new Subject<ContactView>();
    this.removeContactSubject = new Subject<ContactReference>();
    this.newDialogSubject = new Subject<DialogView>();
  }

  setContactInfo(info: ContactInfo): void {
    this.contactInfo = info;
  }

  getContactInfo(): ContactInfo {
    return this.contactInfo;
  }

  setContacts(contacts: ContactInfo[]): void {
    contacts.map(info => {
      let contactView = this.getContactView(info);
      this.contactViews.set(info.contactReference.id, contactView);
    });
  }

  getContacts(): ContactView[] {
    return Array.from(this.contactViews.values())
      .filter(contact => contact.contactReference.id !== this.contactInfo.contactReference.id)
      .sort((a, b) => a.name.localeCompare(b.name));
  }

  isContactOnline(contactReference: ContactReference): boolean {
    return this.contactViews.has(contactReference.id)
  }

  addContact(contactInfo: ContactInfo): void {
    if (!this.contactViews.has(contactInfo.contactReference.id)) {
      let contactView = this.getContactView(contactInfo);
      this.setContactLastMessage(contactInfo, contactView);
      this.contactViews.set(contactInfo.contactReference.id, contactView);
      this.addContactSubject.next(contactView);
    }
  }

  removeContact(contactInfo: ContactInfo): void {
    if (this.contactViews.has(contactInfo.contactReference.id)) {
      this.contactViews.delete(contactInfo.contactReference.id);
      this.removeContactSubject.next(contactInfo.contactReference);
    }
  }

  setDialogs(dialogs: DialogInfo[]) {
    dialogs.map(info => this.dialogViews.set(info.dialogReference.id, this.getDialogView(info)));
  }

  getDialogs(): DialogView[] {
    return Array.from(this.dialogViews.values())
      .sort((a, b) => b.lastMessageDate.localeCompare(a.lastMessageDate));
  }

  addDialog(dialogInfo: DialogInfo): void {
    if (!this.dialogs.has(dialogInfo.dialogReference.id)) {
      this.dialogViews.set(dialogInfo.dialogReference.id, this.getDialogView(dialogInfo));
      this.dialogs.set(dialogInfo.dialogReference.id, []);
    }
  }

  setMessages(dialogReference: DialogReference, messages: Message[]): void {
    this.dialogs.set(dialogReference.id, messages.map(message => this.getMessageView(message)));
  }

  getMessages(dialogReference: DialogReference): MessageView[] {
    return this.dialogs.get(dialogReference.id)
  }

  addMessage(message: Message): void {
    let messageView = this.getMessageView(message);
    if (this.dialogs.has(message.dialogReference.id)) {
      this.dialogs.get(message.dialogReference.id).push(messageView);
      this.dialogViews.get(message.dialogReference.id).lastMessage = this.trimMessage(message.value);
      this.dialogViews.get(message.dialogReference.id).lastMessageDate = message.date;
    } else {
      this.dialogs.set(message.dialogReference.id, [messageView]);
      let newDialogView: DialogView = {
        dialogReference: message.dialogReference,
        contactReference: message.contactInfo.contactReference,
        name: message.contactInfo.name,
        avatar: message.contactInfo.avatar,
        lastMessage: this.trimMessage(message.value),
        lastMessageDate: message.date
      };
      this.dialogViews.set(newDialogView.dialogReference.id, newDialogView);
      this.newDialogSubject.next(newDialogView);
    }
    let contactReference = this.dialogViews.get(message.dialogReference.id).contactReference;
    if (this.contactViews.has(contactReference.id)) {
      this.contactViews.get(contactReference.id).lastMessage = this.trimMessage(message.value);
      this.contactViews.get(contactReference.id).lastMessageDate = message.date;
    }
  }

  setContactsLastMessages(dialogs: DialogInfo[]): void {
    dialogs.forEach(dialog => {
      let interlocutor = dialog.users.find(user => user.contactReference.id !== this.contactInfo.contactReference.id);
      if (this.contactViews.has(interlocutor.contactReference.id)) {
        this.contactViews.get(interlocutor.contactReference.id).lastMessage = this.trimMessage(dialog.lastMessage);
        this.contactViews.get(interlocutor.contactReference.id).lastMessageDate = dialog.lastMessageDate;
      }
    })
  }

  private setContactLastMessage(contactInfo: ContactInfo, contactView: ContactView) {
    this.dialogViews.forEach(value => {
      if (value.contactReference.id === contactInfo.contactReference.id) {
        contactView.lastMessage = value.lastMessage;
        contactView.lastMessageDate = value.lastMessageDate;
      }
    });
  }

  private getContactView(contactInfo: ContactInfo): ContactView {
    return {
      contactReference: contactInfo.contactReference,
      name: contactInfo.name,
      avatar: contactInfo.avatar,
      lastMessage: "",
      lastMessageDate: ""
    }
  }

  private getMessageView(message: Message): MessageView {
    return {
      contactReference: message.contactInfo.contactReference,
      contactName: message.contactInfo.name,
      contactAvatar: message.contactInfo.avatar,
      message: message.value,
      date: message.date
    }
  }

  private getDialogView(dialogInfo: DialogInfo): DialogView {
    let interlocutor = dialogInfo.users.find(user => user.contactReference.id !== this.contactInfo.contactReference.id);
    let lastMessageTrimmed = this.trimMessage(dialogInfo.lastMessage);
    return {
      dialogReference: dialogInfo.dialogReference,
      contactReference: interlocutor.contactReference,
      name: interlocutor.name,
      avatar: interlocutor.avatar,
      lastMessage: lastMessageTrimmed,
      lastMessageDate: dialogInfo.lastMessageDate
    }
  }

  private trimMessage(value: string): string {
    return value.length > 26 ? value.slice(0, 26).concat("...") : value;
  }

}
