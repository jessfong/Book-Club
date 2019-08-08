using System;
using System.Security.Cryptography;

namespace BookClubServer.Helpers
{
    public class PasswordHasher
    {
        private const int saltSize = 16;
        private const int hashSize = 20;

        /// <summary>
        /// Creates a salt and hash for the passed in password
        /// </summary>
        /// <param name="password"> Original password in plain text </param>
        /// <param name="iterations"> Number of iterations </param>
        /// <returns> The hash </returns>
        public string Hash(string password, int iterations)
        {
            // Create salt
            byte[] salt;
            new RNGCryptoServiceProvider().GetBytes(salt = new byte[16]);

            // Create hash
            var pbkdf2 = new Rfc2898DeriveBytes(password, salt, iterations);
            byte[] hash = pbkdf2.GetBytes(hashSize);

            // Combine slat and hash
            byte[] hashBytes = new byte[hashSize + saltSize];
            Array.Copy(salt, 0, hashBytes, 0, saltSize);
            Array.Copy(hash, 0, hashBytes, saltSize, hashSize);

            // Convert to base64
            var base64Hash = Convert.ToBase64String(hashBytes);

            // Form has hwith extra info
            return string.Format($"$HashValue$V1${iterations}${base64Hash}");
        }
        
        /// <summary>
        /// Passes original password to Hash function above to convert 
        /// it to it's hashed equivalent
        /// </summary>
        /// <param name="password"> Original password in plain text </param>
        /// <returns> Hashed password </returns>
        public string Hash(string password)
        {
            return Hash(password, 1000);
        }

        /// <summary>
        /// Checks if the passed in string is a hash that was created with our function from above
        /// </summary>
        /// <param name="hashString"> String to check </param>
        /// <returns> True or false depending on if the string contains the text 'HashValue' </returns>
        public bool IsHashSupported(string hashString)
        {
            return hashString.Contains("$HashValue$V1");
        }

        /// <summary>
        /// Compares a password with it's hash
        /// </summary>
        /// <param name="password"> String to check </param>
        /// <param name="hashedPassword"> Hash being checked with </param>
        /// <returns></returns>
        public bool Verify(string password, string hashedPassword)
        {
            // Check hash
            if (!IsHashSupported(hashedPassword))
            {
                return false;
            }

            // Extract iteration and base64 string
            var splittedHashString = hashedPassword.Replace("$HashValue$V1$", "").Split('$');
            var iterations = int.Parse(splittedHashString[0]);
            var base64Hash = splittedHashString[1];

            // Get hash bytes
            var hashBytes = Convert.FromBase64String(base64Hash);

            // Get salt
            var salt = new byte[saltSize];
            Array.Copy(hashBytes, 0, salt, 0, saltSize);

            // Create hash with given salt
            var pbkdf2 = new Rfc2898DeriveBytes(password, salt, iterations);
            byte[] hash = pbkdf2.GetBytes(hashSize);

            // Get result
            for (var i = 0; i < hashSize; i++)
            {
                if (hashBytes[i + saltSize] != hash[i])
                {
                    return false;
                }
            }

            return true;
        }
    }
}
  