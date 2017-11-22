import {Component, OnInit} from "@angular/core";
import {CommunicationService} from "../../../providers/communication.service";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  public settings = [
    {
      id: 0,
      name: "Log out",
      description: "Exit from application",
      icon: "fa-sign-out"
    },
    {
      id: 1,
      name: "About",
      description: "About ssfm",
      icon: "fa-info-circle"
    }
  ];
  public selected: number;

  constructor(private service: CommunicationService) {
  }

  ngOnInit() {
  }

  select(id: number) {
    this.selected = id;
    switch (id) {
      case 0:
        this.service.logout();
        break;
      case 1:
        this.service.selectAboutSubject.next(true);
        break;
    }
  }

}
