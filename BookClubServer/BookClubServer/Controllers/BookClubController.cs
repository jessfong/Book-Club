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
        private IBookClubServices _bookClubServices;

        public BookClubController(IBookClubServices bookClubServices)
        {
            _bookClubServices = bookClubServices;
        }

        /// <summary>
        /// Creates new user
        /// </summary>
        /// <param name="userCreateModel"> User data to create account with </param>
        /// <returns> New user or error message </returns>
        [HttpPost]
        public async Task<IActionResult> RegisterNewUser([FromBody] UserCreateModel userCreateModel)
        {
            var result = await _bookClubServices.RegisterNewUserAsync(userCreateModel);

            if (result != null)
            {
                return new JsonResult($"Created {result.Username} {result.Password}");
            }

            return new JsonResult("Error creating new user");
        }

        /// <summary>
        /// Checks if username and password are valid for login
        /// </summary>
        /// <param name="user"> User to look for </param>
        /// <returns> If user is valid or not </returns>
        [HttpPost]
        public async Task<IActionResult> SignIn([FromBody] User user)
        {
            var result = await _bookClubServices.SignIn(user);

            if (result != null)
            {
                return Ok();
            }

            return NotFound();
        }
    }
}