import { Component, Input } from '@angular/core';
import { BudgetEntry, BudgetSummary, CategoryBreakdown, MonthlyTrend } from '../models';

@Component({
  selector: 'app-reports-screen',
  templateUrl: './reports-screen.component.html',
  standalone: false
})
export class ReportsScreenComponent {
  @Input() entries: BudgetEntry[] = [];
  @Input() summary: BudgetSummary = { totalIncome: 0, totalExpense: 0, balance: 0 };

  get categoryBreakdown(): CategoryBreakdown[] {
    const totals = new Map<string, number>();
    this.entries.forEach((entry) => totals.set(entry.category, (totals.get(entry.category) ?? 0) + Number(entry.amount)));
    return Array.from(totals.entries())
      .map(([category, total]) => ({ category, total }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 6);
  }

  get monthlyTrend(): MonthlyTrend[] {
    return Array.from({ length: 6 }, (_, index) => {
      const date = new Date();
      date.setMonth(date.getMonth() - (5 - index));
      const amounts = this.entries.filter((entry) => {
        const entryDate = new Date(entry.entryDate);
        return entryDate.getMonth() === date.getMonth() && entryDate.getFullYear() === date.getFullYear();
      });
      return {
        label: date.toLocaleString('en-US', { month: 'short' }),
        income: amounts.filter((entry) => entry.type === 'INCOME').reduce((sum, entry) => sum + Number(entry.amount), 0),
        expense: amounts.filter((entry) => entry.type === 'EXPENSE').reduce((sum, entry) => sum + Number(entry.amount), 0)
      };
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  percentageOfTotal(value: number): number {
    const total = this.summary.totalIncome + this.summary.totalExpense;
    return total ? value / total * 100 : 0;
  }
}
