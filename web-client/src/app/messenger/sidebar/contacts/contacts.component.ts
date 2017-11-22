import {Component, OnDestroy, OnInit} from "@angular/core";
import {ContactReference, ContactView} from "../../../model/model";
import {CommunicationService} from "../../../providers/communication.service";
import {ModelService} from "../../../providers/model.service";
import {Subscription} from "rxjs/Subscription";

@Component({
  selector: 'app-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['./contacts.component.css']
})
export class ContactsComponent implements OnInit, OnDestroy {

  public contacts: ContactView[];
  public selected: ContactReference;
  private removeContactSubscription: Subscription;

  constructor(private service: CommunicationService, private modelService: ModelService) {
    this.contacts = [];
  }

  ngOnInit() {
    this.listContacts(true);
    this.modelService.addContactSubject.subscribe(contact => {
      this.contacts.push(contact);
      this.contacts = this.contacts.sort((a, b) => a.name.localeCompare(b.name));
    });
    this.removeContactSubscription = this.modelService.removeContactSubject.subscribe(contactReference => {
      this.listContacts(false);
    })
  }

  ngOnDestroy() {
    this.removeContactSubscription.unsubscribe();
  }

  listContacts(selectFirst: boolean) {
    this.contacts = this.modelService.getContacts();
    if (selectFirst) {
      if (this.contacts.length !== 0 && !!this.contacts[0]) this.select(this.contacts[0].contactReference);
    }
  }

  select(contactReference: ContactReference) {
    this.selected = contactReference;
    this.service.processDialog(contactReference);
  }

}
