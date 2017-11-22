import {Component, Input, OnInit} from "@angular/core";
import {MessageView} from "../../../model/model";

@Component({
  selector: 'message-view',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageViewComponent implements OnInit {

  @Input()
  message: MessageView;

  @Input()
  me: boolean | undefined;

  constructor() {
  }

  ngOnInit() {
  }

  trimDate(value: string): string {
    return value.slice(0, value.length - 4);
  }

}
