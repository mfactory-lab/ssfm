import {Injectable} from "@angular/core";
import {
  ContactInfo,
  ContactReference,
  DialogInfo,
  DialogReference,
  Message,
  StatusChangedEvent,
  UserInfo
} from "../model/model";
import * as SocketIO from "socket.io-client";
import {Subject} from "rxjs/Subject";
import {AuthService} from "./auth/auth.service";
import {ModelService} from "./model.service";

const ScalaJs = require('../scalajs/scala-js-fastopt.js');
// const ScalaJs = require('../scalajs/scala-js-opt.js');

@Injectable()
export class CommunicationService {

  private loggedIn: boolean;

  public loginSubject: Subject<ContactInfo>;
  public selectDialogSubject: Subject<DialogReference>;
  public selectAboutSubject: Subject<boolean>;

  private contactsStatusChangeSubject: Subject<StatusChangedEvent>;
  private newMessagesSubject: Subject<Message>;

  private host: string = "http://localhost:3000";
  private socket: SocketIOClient.Socket;
  private transportService: any | undefined;

  constructor(private authService: AuthService, private modelService: ModelService) {
    this.socket = SocketIO(this.host);
    this.transportService = new ScalaJs.TransportService(this.socket);

    this.loggedIn = false;
    this.loginSubject = new Subject<ContactInfo>();
    this.selectDialogSubject = new Subject<DialogReference>();
    this.selectAboutSubject = new Subject<boolean>();
    this.contactsStatusChangeSubject = new Subject<StatusChangedEvent>();
    this.newMessagesSubject = new Subject<Message>();
  }

  login(userInfo: UserInfo): void {
    if (!this.loggedIn) {
      this.loggedIn = true;
      this.transportService.addUser(userInfo).then(contactInfo => {
        this.modelService.setContactInfo(contactInfo);
        this.transportService.sendConnect(contactInfo.contactReference);
        this.listen();
        this.initModel().then(() => {
          this.loginSubject.next(contactInfo);
        });
      });
    }
  }

  private initModel(): Promise<boolean> {
    return this.transportService.listContacts()
      .then((contacts: ContactInfo[]) => this.modelService.setContacts(contacts))
      .then(() => {
        this.transportService.listDialogs(this.modelService.getContactInfo().contactReference)
          .then((dialogs: DialogInfo[]) => {
            this.modelService.setDialogs(dialogs);
            this.modelService.setContactsLastMessages(dialogs);
            dialogs.forEach(info => {
              this.transportService.listMessages(info.dialogReference)
                .then((messages: Message[]) => this.modelService.setMessages(info.dialogReference, messages))
                .then(() => {
                  return true;
                });
            });
          })
      });
  }

  private listen(): void {
    this.transportService.listenNewUsers(this.contactsStatusChangeSubject);
    this.transportService.listenNewMessages(this.modelService.getContactInfo().contactReference, this.newMessagesSubject);

    this.contactsStatusChangeSubject.subscribe(statusChangedEvent => {
      if (statusChangedEvent.online) {
        this.modelService.addContact(statusChangedEvent.contact);
      } else {
        this.modelService.removeContact(statusChangedEvent.contact)
      }
    });
    this.newMessagesSubject.subscribe(message => this.modelService.addMessage(message));
  }

  logout(): void {
    this.transportService.logout(this.modelService.getContactInfo());
    this.modelService.setContactInfo(undefined);
    this.authService.logout();
  }

  getDialogInfo(dialogReference: DialogReference): Promise<DialogInfo> {
    return this.transportService.getDialogInfo(dialogReference);
  }

  initializeDialog(dialogReference: DialogReference): void {
    this.selectDialogSubject.next(dialogReference);
  }

  processDialog(contactReference: ContactReference): void {
    this.transportService.getOrCreateDialog(this.modelService.getContactInfo().contactReference, contactReference)
      .then((dialogInfo: DialogInfo) => {
        this.modelService.addDialog(dialogInfo);
        this.initializeDialog(dialogInfo.dialogReference);
      })
  }

  sendMessage(dialogReference: DialogReference, value: string): void {
    let message: Message = {
      dialogReference: dialogReference,
      contactInfo: this.modelService.getContactInfo(),
      value: value,
      date: ""
    };
    this.transportService.sendMessage(message)
  }

}
