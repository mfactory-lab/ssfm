import {Component, Input, OnInit} from "@angular/core";
import {DialogView} from "../../../model/model";

@Component({
  selector: 'dialog-view',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.css']
})
export class DialogViewComponent implements OnInit {

  @Input()
  dialog: DialogView;

  constructor() {
  }

  ngOnInit() {
  }

  trimDate(value: string): string {
    return value.slice(0, value.length - 7);
  }

}
