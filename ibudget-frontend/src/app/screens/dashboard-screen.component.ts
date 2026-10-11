import { Component, Input } from '@angular/core';
import { BudgetEntry } from '../models';

@Component({
  selector: 'app-dashboard-screen',
  templateUrl: './dashboard-screen.component.html',
  standalone: false
})
export class DashboardScreenComponent {
  @Input() entries: BudgetEntry[] = [];
  @Input() incomeCount = 0;
  @Input() expenseCount = 0;

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  formatType(type: 'INCOME' | 'EXPENSE'): string {
    return type === 'INCOME' ? 'Income' : 'Expense';
  }
}
