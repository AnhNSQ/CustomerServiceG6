using System;

namespace CustomerService.Models.Enums
{
    /// <summary>
    /// Enum for ticket priority levels
    /// </summary>
    public enum TicketPriority
    {
        Low = 1,
        Medium = 2,
        High = 3
    }

    /// <summary>
    /// Enum for ticket status
    /// </summary>
    public enum TicketStatus
    {
        Open = 1,
        Assigned = 2,
        InProgress = 3,
        Resolved = 4,
        Closed = 5
    }

    /// <summary>
    /// Enum for order status
    /// </summary>
    public enum OrderStatus
    {
        Pending = 1,
        Paid = 2,
        Shipped = 3,
        Cancelled = 4,
        Completed = 5
    }

    /// <summary>
    /// Enum for shipping status
    /// </summary>
    public enum ShippingStatus
    {
        Pending = 1,
        Processing = 2,
        Shipped = 3,
        Delivered = 4,
        Cancelled = 5
    }

    /// <summary>
    /// Enum for role needed in ticket assignment
    /// </summary>
    public enum RoleNeeded
    {
        FinancialStaff = 1,
        TechnicalSupport = 2
    }

    /// <summary>
    /// Enum for invoice status
    /// </summary>
    public enum InvoiceStatus
    {
        Pending = 1,
        Paid = 2
    }

    /// <summary>
    /// Enum for payment status
    /// </summary>
    public enum PaymentStatus
    {
        Succeeded = 1,
        Failed = 2,
        Refunded = 3
    }

    /// <summary>
    /// Enum for payment methods
    /// </summary>
    public enum PaymentMethod
    {
        Cash = 1,
        CreditCard = 2,
        BankTransfer = 3,
        DigitalWallet = 4
    }
}
