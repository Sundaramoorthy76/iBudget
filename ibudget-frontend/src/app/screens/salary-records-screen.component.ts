import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { Employee, EmployeeForm, EmployeeSalaryRecord, EmployeeSalaryRecordForm, SalaryRecordType } from '../models';

@Component({
  selector: 'app-salary-records-screen',
  templateUrl: './salary-records-screen.component.html',
  standalone: false
})
export class SalaryRecordsScreenComponent implements OnChanges {
  private firstEditableElement?: HTMLSelectElement;
  employeePageIndex = 0;
  employeePageSize = 10;
  recordPageIndex = 0;
  recordPageSize = 10;

  @ViewChild('firstEditable') set firstEditable(element: ElementRef<HTMLSelectElement> | undefined) {
    this.firstEditableElement = element?.nativeElement;
    if (element) {
      this.focusFirstEditable();
    }
  }

  @Input() employees: Employee[] = [];
  @Input() selectedEmployee: Employee | undefined;
  @Input() employeeForm!: EmployeeForm;
  @Input() records: EmployeeSalaryRecord[] = [];
  @Input() form!: EmployeeSalaryRecordForm;
  @Input() editingId: number | null = null;
  @Input() readOnly = false;
  @Input() error = '';
  @Output() selectEmployee = new EventEmitter<Employee>();
  @Output() save = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  @Output() view = new EventEmitter<EmployeeSalaryRecord>();
  @Output() edit = new EventEmitter<EmployeeSalaryRecord>();
  @Output() delete = new EventEmitter<EmployeeSalaryRecord>();
  @Output() back = new EventEmitter<void>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedEmployee']) {
      this.recordPageIndex = 0;
    }
    this.keepPagesInRange();
    if (changes['readOnly'] || changes['editingId'] || changes['form'] || changes['selectedEmployee']) {
      this.focusFirstEditable();
    }
  }

  get visibleEmployees(): Employee[] {
    const start = this.employeePageIndex * this.employeePageSize;
    return this.employees.slice(start, start + this.employeePageSize);
  }

  get visibleRecords(): EmployeeSalaryRecord[] {
    const start = this.recordPageIndex * this.recordPageSize;
    return this.records.slice(start, start + this.recordPageSize);
  }

  changeEmployeePage(index: number): void {
    this.employeePageIndex = index;
  }

  changeEmployeePageSize(size: number): void {
    this.employeePageSize = size;
    this.employeePageIndex = 0;
  }

  changeRecordPage(index: number): void {
    this.recordPageIndex = index;
  }

  changeRecordPageSize(size: number): void {
    this.recordPageSize = size;
    this.recordPageIndex = 0;
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }

  formatType(type: SalaryRecordType): string {
    return type === 'ADVANCE' ? 'Advance' : 'Deduction';
  }

  get typeLabel(): string {
    return this.formatType(this.form.type).toLowerCase();
  }

  private focusFirstEditable(): void {
    if (!this.readOnly && this.selectedEmployee) {
      setTimeout(() => {
        if (!this.readOnly && this.selectedEmployee) {
          this.firstEditableElement?.focus();
        }
      });
    }
  }

  private keepPagesInRange(): void {
    const lastEmployeePage = Math.max(0, Math.ceil(this.employees.length / this.employeePageSize) - 1);
    const lastRecordPage = Math.max(0, Math.ceil(this.records.length / this.recordPageSize) - 1);
    this.employeePageIndex = Math.min(this.employeePageIndex, lastEmployeePage);
    this.recordPageIndex = Math.min(this.recordPageIndex, lastRecordPage);
  }
}
