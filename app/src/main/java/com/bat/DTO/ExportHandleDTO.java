package com.bat.DTO;

    public class ExportHandleDTO {
        private int exportId;
        private String exportDate;
        private int status;
        private double totalPrice;
        private int userId;
        private String workerName;
        private String customerName;

        public ExportHandleDTO(int exportId, String exportDate, int status, double totalPrice, int userId, String workerName, String customerName) {
            this.exportId = exportId;
            this.exportDate = exportDate;
            this.status = status;
            this.totalPrice = totalPrice;
            this.userId = userId;
            this.workerName = workerName;
            this.customerName = customerName;
        }

        public int getExportId() {
            return exportId;
        }
        
        public String getExportDate() {
            return exportDate;
        }

        public int getStatus() {
            return status;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public int getUserId() {
            return userId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public String getCustomerName() {
            return customerName;
        }

    }