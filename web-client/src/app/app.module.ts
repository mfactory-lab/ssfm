import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";

import {AppComponent} from "./app.component";
import {MessengerComponent} from "./messenger/messenger.component";

import {SidebarComponent} from "./messenger/sidebar/sidebar.component";
import {ContactViewComponent} from "./messenger/templates/contact/contact.component";
import {DialogViewComponent} from "./messenger/templates/dialog/dialog.component";
import {SettingViewComponent} from "./messenger/templates/setting/setting.component";
import {MessageViewComponent} from "./messenger/templates/message/message.component"

import {ContactsComponent} from "./messenger/sidebar/contacts/contacts.component";
import {DialogsComponent} from "./messenger/sidebar/dialogs/dialogs.component";
import {SettingsComponent} from "./messenger/sidebar/settings/settings.component";
import {DialogComponent} from "./messenger/dialog/dialog.component";
import {AboutComponent} from './messenger/about/about.component';

import {CallbackComponent} from "./callback/callback.component";

import {Angular2FontawesomeModule} from "angular2-fontawesome/angular2-fontawesome";
import {RouterModule} from "@angular/router";
import {SlimScroll} from "angular-io-slimscroll";

import {AuthService} from "app/providers/auth/auth.service";
import {CommunicationService} from "./providers/communication.service";
import {ModelService} from "./providers/model.service";

@NgModule({
  declarations: [
    AppComponent,
    MessengerComponent,
    SidebarComponent,
    DialogComponent,
    DialogViewComponent,
    ContactsComponent,
    DialogsComponent,
    SettingsComponent,
    ContactViewComponent,
    DialogViewComponent,
    SettingViewComponent,
    MessageViewComponent,
    CallbackComponent,
    AboutComponent,
    SlimScroll
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    Angular2FontawesomeModule,
    RouterModule.forRoot([
      {path: '', component: MessengerComponent},
      {path: 'callback', component: CallbackComponent},
      {path: '**', redirectTo: ''}
    ])
  ],
  providers: [AuthService, CommunicationService, ModelService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
