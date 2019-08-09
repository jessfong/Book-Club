using System.Net;
using System.Threading.Tasks;
using BookClubServer.Data;
using BookClubServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace BookClubServer.Controllers
{
    [Route("api/[controller]/[action]")]
    [ApiController]
    public class BookClubController : ControllerBase
    {
        private readonly IBookClubServices _bookClubServices;

        public BookClubController(IBookClubServices bookClubServices)
        {
            _bookClubServices = bookClubServices;
        }

        /// <summary>
        /// Creates new user if user doesn't already exists and if password is valid
        /// </summary>
        /// <param name="userCreateModel"> User data to create account with </param>
        /// <returns> New user or error message </returns>
        [HttpPost]
        public async Task<IActionResult> RegisterNewUser([FromBody] UserCreateModel userCreateModel)
        {
            if (_bookClubServices.DoesUserExist(userCreateModel.Email))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult($"User with the email {userCreateModel.Email} already exists.");
            }

            if (!_bookClubServices.IsStrongPassword(userCreateModel.Password))
            {
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
                return new JsonResult($"Passwords must include at least one uppercase letter, " +
                    $"one lowercase letter, one number, and a non-alphanumeric character.");
            }

            User user = await _bookClubServices.RegisterNewUserAsync(userCreateModel);

            return new JsonResult(user);
        }

        /// <summary>
        /// Checks if username and password are valid for login
        /// </summary>
        /// <param name="user"> User to look for </param>
        /// <returns> If user is valid or not </returns>
        [HttpPost]
        public IActionResult SignIn([FromBody] User user)
        {
            var result = _bookClubServices.SignIn(user);

            if (result)
            {
                return Ok();
            }

            Response.StatusCode = (int)HttpStatusCode.NotFound;
            return new JsonResult("User was not found");
        }
    }
}