import {Component, OnInit} from '@angular/core';
import {CommunicationService} from "../../providers/communication.service";
import {ContactInfo} from "../../model/model";
import {ModelService} from "../../providers/model.service";

@Component({
  selector: 'app-about',
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css']
})
export class AboutComponent implements OnInit {

  user: ContactInfo;

  constructor(private modelService: ModelService) {
  }

  ngOnInit() {
    this.user = this.modelService.contactInfo;
  }

}
