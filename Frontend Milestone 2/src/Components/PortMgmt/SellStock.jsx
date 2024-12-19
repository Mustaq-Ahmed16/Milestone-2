// SellStock.js

import React, { useState } from "react";
import "./SellStock.css";

const SellStock = ({ portfolioId, stockId, stockSymbol, noOfShares, onClose, onSell }) => {
  const [quantitySell, setQuantitySell] = useState(0);
  const [error, setError] = useState(null);

  const handleSell = async () => {
    if (quantitySell > noOfShares) {
      setError("You cannot sell more shares than you own.");
      return;
    }
    try {
      // Make API call to sell stock
      const response = await fetch(
        `http://localhost:8005/auth/stock/sell-stock/${portfolioId}?stockId=${stockId}&quantitySell=${quantitySell}`,
        {
          method: "POST",
        }
      );
      if (!response.ok) {
        setError("Error connecting to the server. Please try again.");
        return;
      }


      if (response.ok) {
        alert('Successfully sell Stock');
        setError(null);
        onSell(); // Notify parent that sale is successful
        onClose(); // Close dialog
      } else {
        setError("Error selling the stock. Please try again.");
      }
    } catch (err) {
      setError("Error connecting to the server. Please try again.");
    }
  };

  return (
    <div className="sell-stock-dialog">
      <div className="sell-stock-dialog-content">
        <h3>Sell {stockSymbol} Stock</h3>
        <div>
          <label>Shares to sell</label>
          <input
            type="number"
            value={quantitySell}
            onChange={(e) => setQuantitySell(Number(e.target.value))}
            min="1"
            max={noOfShares}
          />
        </div>
        {error && <div className="error">{error}</div>}
        <div className="actions">
          <button onClick={onClose}>Cancel</button>
          <button onClick={handleSell}>Confirm Sell</button>
        </div>
      </div>
    </div>
  );
};

export default SellStock;





// import React, { useState } from "react";

// const SellStock = ({ portfolioId, stockId, stockSymbol, noOfShares, onClose, onSell }) => {
//   const [quantitySell, setQuantitySell] = useState(0);
//   const [error, setError] = useState(null);
//   const [showmsg, setShowMsg] = useState(null);
//   const [isModalOpen, setIsModalOpen] = useState(false);

//   const handleSell = async () => {
//     if (quantitySell > noOfShares) {
//       setError("You cannot sell more shares than you own.");
//       return;
//     }
//     try {
//       // Make API call to sell stock
//       const response = await fetch(
//         `http://localhost:8181/api/stock/sell-stock/${portfolioId}?stockId=${stockId}&quantitySell=${quantitySell}`,
//         {
//           method: "POST",
//         }
//       );
//       if (!response.ok) {
//         setError("Error connecting to the server. Please try again.");
//         return;
//       }

//       // Check if the response data is valid and contains success
//       if (response.ok) {
//         setShowMsg("Stock Sell Successfully Refresh the page to Update");
//         onSell(); // Notify parent that sale is successful
//         onClose(); // Close dialog

//       } else {
//         setError("Error selling the stock. Please try again.");
//       }
//     } catch (err) {
//       setError("Error connecting to the server. Please try again.");
//     }
//   };

//   return (
//     <div className="sell-stock-dialog">
//       <h3>Sell {stockSymbol} Stock</h3>
//       <div>
//         <label>Shares to sell</label>
//         <input
//           type="number"
//           value={quantitySell}
//           onChange={(e) => setQuantitySell(Number(e.target.value))}
//           min="1"
//           max={noOfShares}
//         />
//       </div>
//       {error && <div className="error">{error}</div>}
//       <div className="actions">
//         <button onClick={onClose}>Cancel</button>
//         <button onClick={handleSell}>Confirm Sell</button>
//       </div>
// {isModalOpen && (
//   <div className="modal">
//     <div className="modal-content">
//       <h2>Sell Stock</h2>
//       <label htmlFor="quantitySell">Quantity to Sell:</label>
//       <input
//         type="number"
//         id="quantitySell"
//         placeholder="Enter shares Quantity"
//         value={quantitySell}
//         onChange={(e) => setQuantitySell(e.target.value)}
//       />
//       <button className="submit-btn" onClick={handleSell}>
//         Confirm Sell
//       </button>
//       <button className="close-btn" onClick={() => setIsModalOpen(false)}>
//         Cancel
//       </button>
//     </div>
//   </div>
// )}
//     </div>
//   );
// };

// export default SellStock;
