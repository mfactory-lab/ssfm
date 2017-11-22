import {Component, OnInit} from '@angular/core';
import {CommunicationService} from "../../providers/communication.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  selected: number = 0;

  constructor(private service: CommunicationService) { }

  ngOnInit() {
  }

  select(n: number) {
    this.selected = n;
    if (this.selected < 2) {
      this.service.selectAboutSubject.next(false);
    }
  }

}
