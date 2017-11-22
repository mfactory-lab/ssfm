import {Component, OnInit} from '@angular/core';
import {ContactInfo, DialogInfo, MessageView} from "../../model/model";
import {CommunicationService} from "../../providers/communication.service";
import {ModelService} from "../../providers/model.service";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogComponent implements OnInit {

  dialogInfo: DialogInfo;
  me: ContactInfo;
  interlocutor: ContactInfo;
  isInterlocutorOnline: boolean;
  messages: MessageView[];
  message: string;

  constructor(private service: CommunicationService, private modelService: ModelService) {
    this.me = this.modelService.getContactInfo();
    this.messages = [];
    this.message = "";
  }

  ngOnInit() {
    this.modelService.addContactSubject.subscribe(contact => {
      if (contact.contactReference.id === this.interlocutor.contactReference.id) this.isInterlocutorOnline = true
    });
    this.modelService.removeContactSubject.subscribe(contactReference => {
      if (contactReference.id === this.interlocutor.contactReference.id) this.isInterlocutorOnline = false
    });

    this.service.selectDialogSubject.subscribe(dialogReference => {
      this.service.getDialogInfo(dialogReference)
        .then(dialogInfo => {
          this.dialogInfo = dialogInfo;
          this.interlocutor = dialogInfo.users.find(user => user.contactReference.id !== this.me.contactReference.id);
          this.isInterlocutorOnline = this.modelService.isContactOnline(this.interlocutor.contactReference);
          this.listMessages();
        })
    });
  }

  messageBelongsToCurrentUser(message: MessageView) {
    return message.contactReference.id === this.me.contactReference.id
  }

  send() {
    if (this.message.length > 0) {
      this.service.sendMessage(this.dialogInfo.dialogReference, this.message);
      this.message = "";
    }
  }

  private listMessages(): void {
    this.messages = this.modelService.getMessages(this.dialogInfo.dialogReference);
  }

}
