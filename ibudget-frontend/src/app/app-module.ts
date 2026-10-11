import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { PaginationComponent } from './components/pagination.component';
import { DashboardScreenComponent } from './screens/dashboard-screen.component';
import { EmployeesScreenComponent } from './screens/employees-screen.component';
import { ReportsScreenComponent } from './screens/reports-screen.component';
import { SalaryRecordsScreenComponent } from './screens/salary-records-screen.component';
import { TransactionsScreenComponent } from './screens/transactions-screen.component';

@NgModule({
  declarations: [
    App,
    DashboardScreenComponent,
    TransactionsScreenComponent,
    ReportsScreenComponent,
    EmployeesScreenComponent,
    SalaryRecordsScreenComponent,
    PaginationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners()
  ],
  bootstrap: [App]
})
export class AppModule { }
