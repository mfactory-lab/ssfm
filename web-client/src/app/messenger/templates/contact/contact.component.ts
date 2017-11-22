import {Component, Input, OnInit} from "@angular/core";
import {ContactView} from "../../../model/model";

@Component({
  selector: 'contact-view',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactViewComponent implements OnInit {

  @Input()
  contact: ContactView;

  constructor() {
  }

  ngOnInit() {
  }

  trimDate(value: string): string {
    return value.slice(0, value.length - 7);
  }

}
