import {Component, OnInit} from "@angular/core";
import {DialogReference, DialogView} from "../../../model/model";
import {CommunicationService} from "../../../providers/communication.service";
import {ModelService} from "../../../providers/model.service";

@Component({
  selector: 'app-dialogs',
  templateUrl: './dialogs.component.html',
  styleUrls: ['./dialogs.component.css']
})
export class DialogsComponent implements OnInit {

  public dialogs: DialogView[];
  public selected: DialogReference;

  constructor(private service: CommunicationService, private modelService: ModelService) {
    this.dialogs = [];
  }

  ngOnInit() {
    this.listDialogs(true);
    this.modelService.newDialogSubject.subscribe(dialog => this.dialogs.unshift(dialog));
  }

  listDialogs(selectFirst: boolean) {
    this.dialogs = this.modelService.getDialogs();
    if (selectFirst && this.dialogs.length !== 0 && !!this.dialogs[0]) this.select(this.dialogs[0].dialogReference);
  }

  select(dialogReference: DialogReference) {
    this.selected = dialogReference;
    this.service.initializeDialog(dialogReference);
  }

}
