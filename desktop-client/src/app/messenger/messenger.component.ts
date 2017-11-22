import {Component, OnInit} from "@angular/core";
import {CommunicationService} from "../providers/communication.service";
import {ContactInfo, UserInfo} from "../model/model";
import {AuthService} from "../providers/auth/auth.service";

@Component({
  selector: 'app-messenger',
  templateUrl: './messenger.component.html',
  styleUrls: ['./messenger.component.css']
})
export class MessengerComponent implements OnInit {

  public user: ContactInfo;
  public showAbout: boolean;

  constructor(private authService: AuthService, private service: CommunicationService) {
    this.showAbout = false;
  }

  private appLogin(profile: any) {
    let userInfo: UserInfo = {
      name: profile.name,
      avatarUrl: profile.picture
    };
    this.service.login(userInfo)
  }

  ngOnInit() {
    if (!this.authService.isAuthenticated()) {
      this.authService.login();
    } else {
      if (this.authService.userProfile) {
        this.appLogin(this.authService.userProfile)
      } else {
        this.authService.getProfile((err, profile) => {
          this.appLogin(profile)
        });
      }
    }

    this.service.loginSubject.subscribe(user => {
      this.user = user;
    });

    this.service.selectAboutSubject.subscribe(value => {
      this.showAbout = value;
    });

  }

}
