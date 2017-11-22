import {Component, Input, OnInit} from '@angular/core';
import {SettingView} from "../../../model/model";

@Component({
  selector: 'setting-item',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingViewComponent implements OnInit {

  @Input()
  setting: SettingView;

  constructor() { }

  ngOnInit() {
  }

}
